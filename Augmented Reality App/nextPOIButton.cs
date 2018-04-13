using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class nextPOIButton : MonoBehaviour {

    public GameObject buttonTextObject;
    public AugmentedScript nextPOI;
   // public AugmentedScript rad;

    void Start () {
       buttonTextObject = GameObject.FindGameObjectWithTag("button");
	}
	
	void Update () {}

    public void NextPOI()
    {
        //rad.Show();
        nextPOI.NextPOI();
        buttonTextObject.GetComponent<Text>().text = "En route vers le POI suivant";
    }
}
