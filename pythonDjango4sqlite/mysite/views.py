from django.http import HttpResponse
from django.shortcuts import get_object_or_404, render_to_response
import datetime
from books.models import Book

def hello(request):
	now=datetime.datetime.now()
	#html="<html><body><h3>Hello World!</h3>It is now %s</body></html>" % now
	#html="<html><body><h3>Hello World!</h3>It is now %s</body></html>" % now
        response = HttpResponse()
        response.write("<p>Hello World</p>")
	return response
	#return HttpResponse(html)

def latest_books(request):
	book_list=Book.objects.order_by('-pub_date')[:10]
	return render_to_response('latest_books.html',{'book_list':book_list})
