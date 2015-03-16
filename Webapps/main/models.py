import datetime
from django.utils import timezone


from django.db import models
from django.db import connection
from django.contrib.auth.models import UserManager

class Patient(models.Model):
    patient_id = models.IntegerField(primary_key = True, null=False)
    ahcn = models.CharField(max_length=9, null=True, unique=True)
    dob = models.DateField()
    liveStatus = models.CharField(max_length=1, default ="n")
    doctor = models.CharField(max_length=200, null = True)
    name = models.CharField(max_length=200, null = True)

    class Meta:
        db_table = "patients"
        verbose_name = "Patient"

    def __str__(self):
        return "patient_id: " + str(self.patient_id) + " ahcn: " + str(self.ahcn) + " dob: " + str(self.dob) + " liveStatus: " + str(self.liveStatus) + " doctor: " + str(self.doctor) + " name: " + str(self.name)

class ECG(models.Model):
    patient_id = models.IntegerField(null=False)
    mv = models.IntegerField()
    pulse = models.IntegerField()
    oxygen = models.IntegerField()
    diastolicbp = models.IntegerField()
    systolicbp = models.IntegerField()
    map2 = models.IntegerField()
    timestamp = models.DateTimeField(primary_key = True, auto_now_add=True,null=False)
    session_id = models.IntegerField(null=False)
    
    class Meta:
        db_table = "ecg"
        verbose_name = "ECG"

    def __str__(self):
        return "patient_id: " + str(self.patient_id) + " mv: " + str(self.mv) + " pulse: " + str(self.pulse) + " oxygen: " + str(self.oxygen) + " diastolicbp: " + str(self.diastolicbp) + "systolicbp: " + str(self.systolicbp) + "map: " + str(self.map2) + "timestamp: " + str(self.timestamp) + "session_id: " + str(self.session_id) 


