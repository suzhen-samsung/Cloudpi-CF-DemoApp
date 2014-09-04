backend server1 {
    .host = "10.142.176.85";
    .port = "8080";
}

director sinfo round-robin {
    { 
        .backend = server1;
    }
}

import std;

sub vcl_recv {

    set req.backend = sinfo;

    if (req.url ~ "^/getInterest.do") {
       set req.url = regsub(req.url, "([\?|&]curTime=)(............)([0-9])*.", "\1\200&");
       set req.url = regsub(req.url, "(\?&?)$", "");
       return(lookup);
    }
}

sub vcl_hit {
    return (deliver);
}

sub vcl_hash {
    hash_data(req.url);

    rollback;    
    return(hash);
}

sub vcl_fetch {

    if (req.url ~ "^/getInterest.do") {
       set beresp.ttl=60s;
    }       
    return (deliver);
}
