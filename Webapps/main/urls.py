from django.conf.urls import patterns, include, url
from django.conf import settings
from django.contrib import admin

from main import views

# Django Dynamic URL Tokens by werehuman were used to model urls in the project 
# (http://stackoverflow.com/questions/21693357/django-dynamic-page-functionality-and-url)

urlpatterns = patterns('',
    url(r'^$', views.loginPage, name='LoginPage'),
    url(r'^patientPage/$', views.patientPage, name='patientPage'),
    url(r'^sendpatient/$', views.getPatient, name='getPatient'),
    url(r'^senddata/$', views.getMedData, name='getMedData'),                   
    url(r'^login/$', views.loginPage, name='LoginPage'),
    url(r'^logout/$', views.logout, name="logout"),
    url(r'^(?P<patient_id>.+?)/$', views.patientDisplay, name='patientDisplay'),
    
    
   
   
)

if settings.DEBUG:
    urlpatterns += patterns(
        'django.views.static',
        (r'media/(?P<path>.*)',
        'serve',
        {'document_root': settings.MEDIA_ROOT}), )
