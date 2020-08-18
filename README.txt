
NoSQL database: Redis
Java-based framework: Spring Boot 
CRUD: REST api+POSTMAN
Authentication: OAuth 2.0
Search: Kafka+Elasticsearch+Kibana


- Before running the application, please run elasticsearch, Kibana and kafka.

  - Make sure the kafka configuration(listeners & advertised.listens in server.properties) matches the configuration in this project.

  - The command of running kafka:

    /usr/local/Cellar/kafka/2.5.0/bin/zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &

    /usr/local/Cellar/kafka/2.5.0/bin/kafka-server-start /usr/local/etc/kafka/server.properties &


- CRUD client: Postman


- Search client: Elasticsearch console
                 http://localhost:5601/app/kibana#/dev_tools/console?_g=()

   - Command example:

     DELETE my_index

     PUT my_index?include_type_name=true
     {
       "mappings": {
         "orm":{
         "properties": {
           "plan_service":{
             "type": "join",
             "relations":{
               "plan":["membercostshare","planservice"],
               "planservice":["service","planservice_membercostshare"]
             }
           }
         }
         }
       }
     }

     GET /my_index/_search
     {
       "query": {
         "match_all": {}
       }
     }

     GET /my_index/_search
     {
       "query": {
         "bool": {
           "must": [
             {
               "match": {
                 "objectType": "service"
               }
             }
           ]
         }
       }
     }

GET /my_index/_search
{
  "query": {
    "has_child": {
      "type": "planservice_membercostshare",
      "query": {
          "match_all": {}
      }
    }
  }
}

GET /my_index/_search
{
  "query":{
    "bool": {
      "must": [
        {
          "match": {
            "creationDate": "02-25-2020"
          }
        },
        {
          "has_child": {
            "type": "membercostshare",
            "query": {
              "range": {
                "copay": {
                "gte": 1
                }
              }
            }
          }
        }
      ]
    }
  }
}

GET /my_index/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "planType": "inNetwork"
          }
        },{
          "has_child": {
            "type": "planservice",
            "query": {
              "has_child": {
                "type": "service",
                "query": {
                  "bool": {
                    "must": [
                      {
                        "match": {
                          "name": "well baby"
                        }
                      }
                    ]
                  }
                }
              }
            }
          }
        }
      ]
    }
  }
}

