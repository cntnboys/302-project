from django.contrib import admin

from main.models import Patient, ECG
# Register your models here.

admin.site.register(Patient)
admin.site.register(ECG)