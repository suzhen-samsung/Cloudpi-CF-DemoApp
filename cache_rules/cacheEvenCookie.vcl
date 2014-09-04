backend server1 {
    .host = "10.142.176.85";
    .port = "8080";
}

backend server2 {
    .host = "10.183.161.80";
    .port = "8080";
}

director sinfo round-robin {
    {
        .backend = server1;
    }
    {
        .backend = server2;
    }
}

import std;

sub vcl_hash {
    hash_data(req.http.cookie);
    return(hash);
}

sub vcl_recv {
    set req.backend = sinfo;
    if (req.url ~ ".jpg") {
        unset req.http.cookie;
    }
    return(lookup);
}

sub vcl_fetch {
    if (req.url ~ ".jpg") {
        unset beresp.http.set-cookie;
    }
    return (deliver);
}

