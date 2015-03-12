from django.conf.urls import patterns, include, url
from django.conf import settings
from django.contrib import admin

from django.conf.urls.static import static
import main.views


from rest_framework import routers, serializers, viewsets

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', '_project410.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^$', main.views.loginPage, name='LoginPage'),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^main/', include('main.urls')),
  
    
    
   
)+ static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)+ static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

if settings.DEBUG:
    urlpatterns += patterns(
        'django.views.static',
        (r'media/(?P<path>.*)',
        'serve',
        {'document_root': settings.MEDIA_ROOT}), )
