match-query-service
=================

Match Query Service provides API's to serve the user matches feed information.

Service will handle the requests to serve the feed data

1. Get Match From HBase Only
   http://localhost:9002/mymatches/v1//users/{userId}/matches/{matchId}
   
2. Get Matches From Hbase
   http://localhost:9002/mymatches/v1/internal/users/{userId}/matches
 
3. Merge between Voldy and HBase (Profile data)
   http://localhost:9002/mymatches/v1/users/{userId}/matches