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

sub vcl_recv {
   set req.backend = sinfo;
   if (req.http.user-agent ~ "iP(hone|od)") {
      error 750 "Moved Temporarily";
   }
}
 
sub vcl_error {
    if (obj.status == 750) {
        set obj.http.Location = "http://www.example.com/iphoneversion/";
	set obj.status = 302;
        return(deliver);
    }
}
