# banner ads service

This is a Java application for a banner ads service.

 — It makes a periodical load of ad content from a provider (e.g. eBay). The ads can be served from the ads service and not directly from the provider removing the provider limitation on API calls. 
 — The ads can be filtered/enhanced before downloading based on the rules (drools).
 — It creates banner ad images from the image and textual content with Apache FOP.
 — The advertising sites can use a custom JavaScript code to get banners. The banners can be static or rotating. 
 — The delivery of the ad banner can be direct if a natural id is used or the ad service may decide which banner to serve based on submitted criteria using rules and search functionality.  
 — The administration and statistical analysis of the ad service is done with Angular client.
 
 MongoDB Docker setup:
 docker run -d -p 27017:27017 --name mongodb mongo