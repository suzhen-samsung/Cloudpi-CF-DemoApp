# models.py (the database tables)

from django.db import models

class Book(models.Model):
    name = models.CharField(max_length=50)
    pub_date = models.DateField()

class Publisher(models.Model):
    name = models.CharField(max_length=50)
    pub_date = models.DateField()

class Author(models.Model):
    name = models.CharField(max_length=50)
    pub_date = models.DateField()
