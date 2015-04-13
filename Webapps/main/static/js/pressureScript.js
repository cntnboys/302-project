
function sleep(milliseconds) {
var start = new Date().getTime();
  for (var i = 0; i < 1e7; i++) {
    if ((new Date().getTime() - start) > milliseconds){
      break;
    }
  }
}
var j=0;
var i =0;
var Trying = true;
function httpGet() {
    //  if (Trying){
        //try{
        sleep(1000);
        var xmlHttp= new XMLHttpRequest();
        var ptid=document.getElementById('pId').innerHTML;
        ptid.trim();
        var pageString = "https://project302.herokuapp.com/main/getPatientdata/";
        var slash ="/";
        var page= pageString.concat(ptid);
        page.trim();
        var pageFinal= page.concat(slash);
        pageFinal.trim();
      //  window.alert(pageFinal);
        xmlHttp.open("GET",pageFinal,false);
        xmlHttp.send(null);



//  var dataString = "{'ecgobject': [[<ECG: patient_id: 1 mv: 12 pulse: 12 oxygen: 12 diastolicbp: 12systolicbp: 12map: 12timestamp: 2015-03-15 04:42:38.315322+00:00session_id: 123>]]}"
    //window.alert(xmlHttp.responseText);
    //window.alert(xmlHttp.responseText);
        var dataString= xmlHttp.responseText;
        stringToData(dataString);
    //    }
      //  catch(err){
        //  Trying = false;
          //var dataString = "{'ecgobject': [[<ECG: patient_id: 1 mv: 12 pulse: 12 oxygen: 12 diastolicbp: 12systolicbp: 12map: 12timestamp: 2015-03-15 04:42:38.315322+00:00session_id: 123>]]}"
          //stringToData(dataString);
        //}
      //}
}
var pulseRat,diaLoc,sysLoc,mapLoc,oxySub,diaCal,sysCal;
function stringToData(requestString){
    var pulseLoc= requestString.search("pulse:");//seven for lenght of "pulse: "
    var oxygenLoc=requestString.search("oxygen"); //+8
    var diaLoc=requestString.search("diastolicbp");//+13
    var sysLoc=requestString.search("systolicbp");//+12
    var mapLoc=requestString.search("map");


    var oxySub=requestString.slice(oxygenLoc+8,diaLoc-1);
    var diaSub=requestString.slice(diaLoc+13,sysLoc);
    var sysSub=requestString.slice(sysLoc+12,mapLoc);

    var pulseSub=requestString.slice(pulseLoc+7,oxygenLoc-1);
    pulseRat=parseInt(pulseSub);
    oxyCal=parseInt(oxySub),
    diaCal=parseInt(diaSub),
    sysCal=parseInt(sysSub);
    //
    //window.alert(pulseSub);

}
var diaRate=0,sysRate=0,oxRate=0;
function bploop(){
    httpGet();
    var dia=document.getElementById('dia');
    var sys=document.getElementById('sys');
    var oxim=document.getElementById('oxim');

    diaRate=diaCal;
    sysRate=sysCal;
    oxRate=oxyCal;

    dia.innerHTML=diaRate;
    sys.innerHTML=sysRate;
    oxim.innerHTML=oxRate;

    if(sysRate<100){
        sys.style.color="red";
    }else if(sysRate>200){
        sys.style.color='red';
    }else{
        sys.style.color='green';
    }

    if(diaRate<=110){
        dia.style.color="green";
    }else {
        dia.style.color='red';
    }
    if(oxRate<94){
        oxim.style.color="red";
    }else{
        oxim.style.color='green';
    }

    //setInterval(bploop,1000);
    requestAnimationFrame(bploop);
}
bploop();
