using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Net;
using System.IO;
using System;

public class AugmentedScript : MonoBehaviour
{
    //Initialization of the variables
    public string[] jsonItems;
    private int counter;
    private double distance;
    private double[,] coordonnees;
    private bool setOriginalValues = true;
    private float speed = .1f;
    private float x1, z1, x2, z2;
    private float originalLatitude;
    private float originalLongitude;
    private float currentLongitude;
    private float currentLatitude;
    private float latModel = (float)48.833277;
    private float lngModel = (float)2.250200;
    private float distanceBetweenGPSModel;
    private float lat2 = (float)48.83333; //ma chambre
    private float lng2 = (float)2.2503;
    private float lat1, lng1;
    private Vector3 destinationLRend = new Vector3(0, -150, 0);
    private Vector3 targetPosition;
    private Vector3 originalPosition;
    private LineRenderer lRend;
    private GameObject lineRendererObject;
    private GameObject distanceTextObject;
    private GameObject originalPositionTextObject;
    private GameObject currentPositionTextObject;
    private GameObject flyingObject;
    private GameObject modelObject;
    private GameObject distanceBetweenTextObject;
    private GameObject orientationTextObject;
    private GameObject nomPOIObject;
    private GameObject ButtonObject;
    private GameObject ButtonObject2;
    private GameObject flecheNordObject;
    private GameObject flecheEstObject;
    private GameObject flecheOuestObject;
    private GameObject flecheSudObject;
    private GameObject flecheNordEstObject;
    private GameObject flecheSudEstObject;
    private GameObject flecheSudOuestObject;
    private GameObject flecheNordOuestObject;
    private GameObject arriveeObject;

    IEnumerator GetCoordinates()
    {
        //while true so this function keeps running once started.
        while (true)
        {
            // check if user has location service enabled
            if (!Input.location.isEnabledByUser)
                yield break;

            // Start service before querying location
            Input.location.Start(1f, .1f);

            // Wait until service initializes
            int maxWait = 20;
            while (Input.location.status == LocationServiceStatus.Initializing && maxWait > 0)
            {
                yield return new WaitForSeconds(1);
                originalPositionTextObject.GetComponent<Text>().text = " ligne 104";
                maxWait--;
            }

            // Service didn't initialize in 20 seconds
            if (maxWait < 1)
            {
                print("Timed out");
                originalPositionTextObject.GetComponent<Text>().text = " ligne 112";
                yield break;
            }

            // Connection has failed
            if (Input.location.status == LocationServiceStatus.Failed)
            {
                print("Unable to determine device location");
                originalPositionTextObject.GetComponent<Text>().text = " ligne 120";
                yield break;
            }
            else
            {
                // Access granted and location value could be retrieved
                print("Location: " + Input.location.lastData.latitude + " " + Input.location.lastData.longitude + " " + Input.location.lastData.altitude + " " + Input.location.lastData.horizontalAccuracy + " " + Input.location.lastData.timestamp);

                //if original value has not yet been set save coordinates of player on app start
                if (setOriginalValues)
                {
                    originalLatitude = Input.location.lastData.latitude;
                    originalLongitude = Input.location.lastData.longitude;
                    // originalPositionTextObject.GetComponent<Text>().text = "Original lat/lon : " + originalLatitude + " ; " + originalLongitude;
                    setOriginalValues = false;
                }

                //overwrite current lat and lon everytime
                currentLatitude = Input.location.lastData.latitude;
                currentLongitude = Input.location.lastData.longitude;
                // currentPositionTextObject.GetComponent<Text>().text = "Current lat/lon : " + currentLatitude + " ; " + currentLongitude;

                //calculate the distance between where the player was when the app started and where they are now.         
                Calc(originalLatitude, originalLongitude, currentLatitude, currentLongitude);
                RotateArrow(currentLatitude, currentLongitude);
                originalPositionTextObject.GetComponent<Text>().text = " Lat initiale:  " + originalLatitude + "    Lng initiale:  " + originalLongitude;
                currentPositionTextObject.GetComponent<Text>().text = " Lat actuelle:" + currentLatitude + "    Lng actuelle:" + currentLongitude;
                ToggleDisplayModel(currentLatitude, currentLongitude);
                
                
                destinationLRend = new Vector3((lng1 - currentLongitude) * 2500000f, -500f,(lat1 - currentLatitude) * 2500000f);
                
               //lRend.SetPosition()
            }
            Input.location.Stop();
        }
    }

    //calculates distance between two sets of coordinates, taking into account the curvature of the earth.
    public void Calc(float lat1, float lon1, float lat2, float lon2)
    {

        var R = 6378.137; // Radius of earth in KM
        var dLat = lat2 * Mathf.PI / 180 - lat1 * Mathf.PI / 180;
        var dLon = lon2 * Mathf.PI / 180 - lon1 * Mathf.PI / 180;
        float a = Mathf.Sin(dLat / 2) * Mathf.Sin(dLat / 2) +
          Mathf.Cos(lat1 * Mathf.PI / 180) * Mathf.Cos(lat2 * Mathf.PI / 180) *
          Mathf.Sin(dLon / 2) * Mathf.Sin(dLon / 2);
        var c = 2 * Mathf.Atan2(Mathf.Sqrt(a), Mathf.Sqrt(1 - a));
        distance = R * c;
        //convert distance from double to float
        float distanceFloat = (float)distance;
        //set the target position of the ufo, this is where we lerp to in the update function
        targetPosition = originalPosition - new Vector3(0, 0, distanceFloat * 12);
        //distance was multiplied by 12 so I didn't have to walk that far to get the UFO to show up closer

    }

    public void ToggleDisplayModel(float lat, float lng)
    {
        distanceBetweenGPSModel = Mathf.Sqrt(Mathf.Pow((lat - latModel),2) + Mathf.Pow((lng - lngModel),2));
        if (distanceBetweenGPSModel<0.0001)
        {
            modelObject.SetActive(true);
        }
        else { modelObject.SetActive(false); }

    }

    //Compute de distance between two points, given their coordinate 
    public float DistanceToPoint(float lat1, float lon1, float lat2, float lon2)
    {
        return Mathf.Sqrt(Mathf.Pow((lat1 - lat2), 2) + Mathf.Pow((lon1 - lon2), 2)); 
    }

    //Rotate the arrows aiming to the next POI
    public void RotateArrow(float lat, float lng)
    {
        float tanAlpha = 0;
        float degAlpha = 0;
        float distanceToPoint = 10000;
        tanAlpha = (float)(lat1 - lat)/(lng1-lng);
        degAlpha = (Mathf.Atan(tanAlpha) * 180) / Mathf.PI;

        if (lng1>lng)
        {
            degAlpha = 180f - degAlpha;
        }
        else
        {
            degAlpha = -degAlpha;
        }
        distanceToPoint = DistanceToPoint(lat, lng, lat1, lng1);
        orientationTextObject.GetComponent<Text>().text = " Distance jusqu'au prochain POI : " + 
            Math.Round(distanceToPoint*100000,2)+ "m ";
        if (0<distanceToPoint && distanceToPoint<0.00012)
        {
            flecheNordObject.transform.eulerAngles = new Vector3(0, (degAlpha), 90);
            flecheEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 90);
            flecheSudObject.transform.eulerAngles = new Vector3(10, (degAlpha), 90);
            flecheOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 90);
            flecheNordEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 90);
            flecheSudEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 90);
            flecheSudOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 90);
            flecheNordOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 90);     
            arriveeObject.SetActive(true);
        }

        else if (0.00012 <= distanceToPoint && distanceToPoint <= 0.0002)
        {
            flecheNordObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);
            flecheEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);
            flecheSudObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);
            flecheOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);
            flecheNordEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);
            flecheSudEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);
            flecheSudOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);
            flecheNordOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 45);

            arriveeObject.SetActive(false);
        }

        else
        {
            flecheNordObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0);
            flecheEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0);
            flecheSudObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0);
            flecheOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0);
            flecheNordEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0);
            flecheSudEstObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0);
            flecheSudOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0);
            flecheNordOuestObject.transform.eulerAngles = new Vector3(0, (degAlpha), 0); 
            arriveeObject.SetActive(false);
        }

    }

    // Those functions are called when the user clicks on the buttons
    // Set the new and previous point that the arrows will point at
    public void NextPOI()
    {
        counter++;
        nomPOIObject.GetComponent<Text>().text = "Point " + (counter + 1) + " sur 4";
        lat1 = (float)coordonnees[counter, 0];
        lng1 = (float)coordonnees[counter, 1];
    }

    public void PrevPOI()
    {
        counter--;
        nomPOIObject.GetComponent<Text>().text = "Point " + (counter +1) + " sur 4";
        lat1 = (float)coordonnees[counter, 0];
        lng1 = (float)coordonnees[counter, 1];
    }



    IEnumerator Start()
    {         
        //get distance text reference and all other references
        distanceTextObject = GameObject.FindGameObjectWithTag("distanceText");
        originalPositionTextObject = GameObject.FindGameObjectWithTag("originalPositionTag");
        currentPositionTextObject = GameObject.FindGameObjectWithTag("currentPositionTag");
        flyingObject = GameObject.FindGameObjectWithTag("ufo");
        modelObject = GameObject.FindGameObjectWithTag("model");
        distanceBetweenTextObject = GameObject.FindGameObjectWithTag("distanceBetween");
        flecheNordObject = GameObject.FindGameObjectWithTag("flecheNord");
        flecheEstObject = GameObject.FindGameObjectWithTag("flecheEst");
        flecheOuestObject = GameObject.FindGameObjectWithTag("flecheOuest");
        flecheSudObject = GameObject.FindGameObjectWithTag("flecheSud");
        flecheNordEstObject = GameObject.FindGameObjectWithTag("flecheNordEst");
        flecheSudEstObject = GameObject.FindGameObjectWithTag("flecheSudEst");
        flecheSudOuestObject = GameObject.FindGameObjectWithTag("flecheSudOuest");
        flecheNordOuestObject = GameObject.FindGameObjectWithTag("flecheNordOuest");
        orientationTextObject = GameObject.FindGameObjectWithTag("orientation");
        arriveeObject = GameObject.FindGameObjectWithTag("arrivee");
        nomPOIObject = GameObject.FindGameObjectWithTag("nomPOI");
        ButtonObject = GameObject.FindGameObjectWithTag("displayButtonTag");
        ButtonObject2 = GameObject.FindGameObjectWithTag("prevPOI");
        lineRendererObject = GameObject.FindGameObjectWithTag("directionLine");
        lRend = lineRendererObject.GetComponent<LineRenderer>();
        lRend.SetPosition(0, new Vector3(0f, -1000f, 0f));
        
        //initialize target and original position
        targetPosition = transform.position;
        originalPosition = transform.position;

        //originalPositionTextObject.SetActive(false);
        modelObject.SetActive(false);
        arriveeObject.SetActive(false);

        // Connection to the PHP Script
        WWW itemsData = new WWW("https://ultra-instinct-ece.000webhostapp.com/idEtCoordonneesPPE.php");
        // Wait until the serveur sends its OK response
        yield return itemsData;
        string itemsDataString = itemsData.text;

        // Convert the serveur query response into a JSON parsable string
        string jsonString = itemsDataString.Replace("},{", "};{");
        jsonString = jsonString.Replace("[{", "{").Replace("}]", "}");
        jsonItems = jsonString.Split(';');

        // Create an array to fill it with coordinates
        coordonnees = new double[jsonItems.Length, 2];
          print(coordonnees.Length);
          for (int i = 0; i < jsonItems.Length; i++)
          {
            // Use Unity library to parse every single json string previously extracted
              Coordonnees coords = JsonUtility.FromJson<Coordonnees>(jsonItems[i]);
              coordonnees[i, 0] = coords.getLat();
              coordonnees[i, 1] = coords.getLng();
          }

          // Set the first coordinate to look at
          lat1 = (float)coordonnees[0, 0];
          lng1 = (float)coordonnees[0, 1];

          for (int i = 0; i < jsonItems.Length; i++) print(lat1 + "Lat :" + (float)coordonnees[i, 0] + " Lng :" + (float)coordonnees[i, 1]);
          //for (int i = 0; i < jsonItems.Length; i++) print("Lat :" + (float)Math.Round(coordonnees[i, 0], 10) + " Lng :" + coordonnees[i, 1]);
        StartCoroutine("GetCoordinates");
    }

    void Update()
    {
        lRend.SetPosition(1, destinationLRend);
        //linearly interpolate from current position to target position
        transform.position = Vector3.Lerp(transform.position, targetPosition, speed);
        //rotate by 1 degree about the y axis every frame
        transform.eulerAngles += new Vector3(0, 1f, 0);
        

    }
}

//Classe permettant d'utliser les outils de manipulation JSON
[System.Serializable]
public class Coordonnees
{
    public int id_way;
    public float latitude;
    public float longitude;

    public int getID() { return this.id_way; }
    public float getLat() { return this.latitude; }
    public float getLng() { return this.longitude; }
}
