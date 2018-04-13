using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class prevPOIButton : MonoBehaviour {

    public GameObject buttonTextObject;
    public AugmentedScript prevPOI;
    // public AugmentedScript rad;

    void Start(){
        buttonTextObject = GameObject.FindGameObjectWithTag("button2");
    }

    void Update () {}

    public void PrevPOI()
    {
        //rad.Show();
        prevPOI.PrevPOI();
        buttonTextObject.GetComponent<Text>().text = "En route vers le POI précédent";
    }
}
