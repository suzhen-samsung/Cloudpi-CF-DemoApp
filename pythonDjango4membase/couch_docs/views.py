from django.http import Http404,HttpResponseRedirect
from django.shortcuts import render_to_response
import memcache
NODES = ['54.193.228.24:11211']


def index(request):
    try:
        mc = memcache.Client(NODES)
        all_stats = mc.set("testkey","testvalue")
        key="testkey"
        value = mc.get(key)
        print value
    except Exception:
        print time.strftime('%Y/%m/%d %H:%M:%S', time.localtime()), 'error'
        sys.exit(1)
    return render_to_response('couch_docs/index.html',{'value':value,'key':key})

def detail(request,id):
    docs = SERVER['docs']
    try:
        doc = docs[id]
    except ResourceNotFound:
        raise Http404        
    if request.method =="POST":
        doc['key'] = request.POST['key'].replace(' ','')
        doc['value'] = request.POST['value']
        docs[id] = doc
    return render_to_response('couch_docs/detail.html',{'row':doc})


