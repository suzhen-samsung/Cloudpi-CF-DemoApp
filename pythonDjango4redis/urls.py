from django.conf.urls import patterns, include, url
from django.contrib import admin
admin.autodiscover()
urlpatterns = patterns("",
    url(r"^static/(?P<path>.*)$", 'django.contrib.staticfiles.views.serve', {'insecure': True}),
    url(r"^", include(admin.site.urls)),
)

