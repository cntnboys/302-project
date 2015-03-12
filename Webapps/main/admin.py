from django.contrib import admin

from main.models import Patient, ECG, Oximeter, BloodPressure
# Register your models here.

admin.site.register(Patient)
admin.site.register(ECG)
admin.site.register(Oximeter)
admin.site.register(BloodPressure)