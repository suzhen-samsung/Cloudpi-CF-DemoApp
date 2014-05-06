from django.conf.urls import patterns, include, url
import settings
urlpatterns = patterns('couch_docs.views',
    (r'^$','index'), 
)
