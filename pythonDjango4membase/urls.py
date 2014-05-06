from django.conf.urls import patterns, include, url
import settings

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('comfy_django_example',
    (r'^', include('couch_docs.urls')),
)
