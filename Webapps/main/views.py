import calendar
from datetime import timedelta
import random
import django

from django.contrib import messages
#from django.core.exceptions import ObjectDoesNotExist, ValidationError
from django.db.models import Count
from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import render, redirect, get_object_or_404, render_to_response
from django.template import RequestContext, loader
from django.core.context_processors import csrf

from django.views.decorators.csrf import csrf_exempt


from main.models import Patient, ECG
from django.contrib.auth.models import User
from django.contrib.auth import authenticate, login
from django.contrib.auth import logout as auth_logout
import json


#variables for creating/updating a patient from HTTP requests
idpat = None
ahcn = None
dob = None
liveStatus = None
doctor = None
name = None


#variables for creating/newdata from HTTP requests
hey1 = None
hey2 = None
hey3 = None
hey4 = None
hey5 = None
hey6 = None
hey7 = None 
hey8 = None



#grab patient information
def patientPage(request):

    context = RequestContext(request)
    if request.user.is_authenticated():

        new_patient = Patient.objects.get_or_create(patient_id = "1", ahcn="99-99-99", dob="2009-10-03", liveStatus="True", doctor="DocNa", name="Cameron")

        items = []
        if request.method == "GET":
            for x in Patient.objects.all():
                items.insert(0,x)

        return render(request, "patients.html", {'items' : items})


def patientDisplay(request, patient_id):
    if request.method == "GET":
        context = RequestContext(request)
        if request.user.is_authenticated():
            items = []
            patient = Patient.objects.get(patient_id = patient_id)
            items.append(patient)

            print patient.patient_id

            #geting that patients sessions by checking ECG objects
            new_Ecg = ECG.objects.get_or_create(patient_id = patient.patient_id, mv = 12, pulse = 12, oxygen = 12, diastolicbp= 12, systolicbp = 12, map2 = 12, session_id= 123)
            sessions = []
            Ecgobj = ECG.objects.get(patient_id = patient_id)
            sessions.append(Ecgobj)
        

    return render(request, "panel1.html", {'items' : items, 'sessions' : sessions})



#redirect to home page
def redirecthome(request):
    redirect(patientPage)



# Log in Page function is a check for authenticated author log in
# If author inputs incorrect or non exisiting things in the fields,
# then author will be prompted that either the input was incorrect or
# input does not exist
def loginPage(request):

    if request.method == "POST":

        # Handle if signin not clicked
        if len(request.POST) == 0:
            return render(request, 'login.html')

        username = request.POST.get('username', "").strip()
        password = request.POST.get('password', "").strip()

        #username = "admin"
        #password = "admin"

        error_msg = None

        # Check if fields are filled.
        if username == "admin" and password == "admin":
            
           
            user = authenticate(username=username, password=password)
            # Determine if user exists.
            if user is not None:
                if user.is_active:
                    login(request, user)
            return redirect(patientPage)



      

        else:
            error_msg = "Username or password is not valid. Please try again." 
            return render (request, 'login.html', {'error_msg':error_msg })


    else:
        return render(request, 'login.html')

# Log out function allows user to log out of the current authenticated account
# and the author will be redirected to the intro page.
def logout(request):
    # Logout function redefined in import statement by Chris Morgan
    # http://stackoverflow.com/questions/7357127/django-logout-crashes-python

    context = RequestContext(request)
    auth_logout(request)
    return redirect(loginPage)


# Searching User Page is a function currently unimplemented. This will be a fuction
# that might come in handy for part 2 searching users of another host server.
def searchPage(request):
    items = []
    if request.method == 'POST':
        #searchField = request.POST["searchuser"]
        current_user = request.user
        print current_user.id
        #print request.user.is_authenticated()
        
        # if logged in
        if request.user.is_authenticated():
            for e in Friends.objects.filter(inviter_id=current_user):
                if e.status is True :
                    a = Authors.objects.filter(author_uuid=e.invitee_id.author_uuid)
                    items.append(a)
            #print a.values('name')
            
            for e in Friends.objects.filter(invitee_id=current_user):
                if e.status is True :
                    a = Authors.objects.filter(author_uuid=e.inviter_id.author_uuid)
                    items.append(a)

    return render(request, 'search.html',{'items':items})
 


#create patient or update patient 
def create_pat(idpat, ahcn, dob, liveStatus, doctor, name):
     print("patient created")
     
     if liveStatus == "n":

         Patient.objects.filter(patient_id = idpat).update(liveStatus="n")

     else:
        
         created = Patient.objects.get_or_create(patient_id = idpat, ahcn= ahcn, dob= dob, liveStatus= liveStatus, doctor= doctor, name= name)
     



#function for adding in Data
def add_data(p_id,mv1,oxygen1,diastolicbp1,systolicbp1,map21,session_id1,pulse):
    print("data added")
    ECG.objects.get_or_create(patient_id = p_id , mv = mv1,pulse = pulse, oxygen=oxygen1, diastolicbp=diastolicbp1, systolicbp=systolicbp1, map2=map21, session_id=session_id1 )


     

#getapatient to initiliaze or update:

@csrf_exempt
def getPatient(request):
    context = RequestContext(request)

    if request.method == "POST":
        data = json.loads(request.body)
        print 'Patient Data: "%s"' % request.body 

        patient_id2 = str(data['patient_id'])

        idpat = patient_id2

        print(patient_id2)

        ahcn2 = str(data['ahcn'])

        ahcn = ahcn2

        print(ahcn2)

        dob2 = str(data['dob'])

        dob = dob2

        print(dob2)

        liveStatus2 = str(data['liveStatus'])

        liveStatus = liveStatus2

        print(liveStatus2)

        doctor2 = str(data['doctor'])

        doctor = doctor2

        print(doctor2)

        name2 = str(data['name'])

        name = name2

        print(name2)
      

       # created = Patient.objects.get_or_create(patient_id = patient_id2, ahcn= ahcn, dob= dob2, liveStatus= liveStatus2, doctor= doctor2, name=name2)
        create_pat(idpat,ahcn,dob,liveStatus,doctor,name)

       

    #get what feild you need from json object example
    #custom_decks = data['custom_decks']
    
    return HttpResponse("OK")
  
  

#getting in data from android app:
@csrf_exempt
def getMedData(request):
    
    if request.method == "POST":

        data = json.loads(request.body)

        
        p_id = str(data["patient_id"])

        hey1 = p_id

        print("pid:",p_id)

        mv1 = str(data["mv"])

        hey2 = mv1

        print("mv:",mv1)

        oxygen1 = str(data["oxygen"])

        hey3 = oxygen1

        print("oxygen:",oxygen1)


        diastolicbp1 = (data["diastolicbp"])

        hey4 = diastolicbp1

        print("dias:", diastolicbp1)

        systolicbp1 = (data["systolicbp"])

        hey5 = systolicbp1

        print("sys:",systolicbp1)


        map21 = str(data["map2"])

        hey6 = map21


        print("map:",map21)


        session_id1 = str(data["session_id"])
        

        hey7 = session_id1
    
        print("session:",session_id1)

        pulse_1 = str(data["pulse"])

        print("pulse_1", pulse_1)

        hey8 = pulse_1

        add_data(hey1,hey2,hey3,hey4,hey5,hey6,hey7,hey8)

       # patient_data = ECG.objects.create(patient_id = p_id ,mv = mv1, oxygen=oxygen1, diastolicbp=diastolicbp1, systolicbp=systolicbp1, map2=map21, timestamp=timestamp1, session_id=session_id1 )
        #patient_data = ECG.objects.create(patient_id = p_id ,mv = mv1, oxygen=oxygen1, diastolicbp=diastolicbp1, systolicbp=systolicbp1, map2=map21, timestamp=timestamp1, session_id=session_id1 )


     #get what feild you need from json object example
     #custom_decks = data['custom_decks']
     
    return HttpResponse("OK")
