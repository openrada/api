define({ "api": [
  {
    "type": "get",
    "url": "/parliament/:convocation/members/:id",
    "title": "Get full information for the specific parliament member.",
    "name": "GetMember",
    "group": "Members",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "convocation",
            "description": "<p>Parliament convocation. Current one is 8th.</p> "
          },
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "id",
            "description": "<p>Parliament member id.</p> "
          }
        ]
      }
    },
    "examples": [
      {
        "title": "Example usage:",
        "content": "curl -i https://api.openrada.com/v1/parliament/8/memebers/005ded9a-18c4-4f34-806c-80a82e9a7a26",
        "type": "shell"
      }
    ],
    "version": "0.0.0",
    "filename": "./src/openrada/api/http.clj",
    "groupTitle": "Members"
  },
  {
    "type": "get",
    "url": "/parliament/:convocation/members",
    "title": "Get all members of the parliament from the specific convocation.",
    "name": "GetMembers",
    "group": "Members",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "convocation",
            "description": "<p>Parliament convocation. Current one is 8th.</p> "
          }
        ]
      }
    },
    "examples": [
      {
        "title": "Example usage:",
        "content": "curl -i https://api.openrada.com/v1/parliament/8/memebers",
        "type": "shell"
      }
    ],
    "success": {
      "fields": {
        "200": [
          {
            "group": "200",
            "type": "Object[]",
            "optional": false,
            "field": "members",
            "description": "<p>List of parliament members.</p> "
          },
          {
            "group": "200",
            "type": "String",
            "optional": false,
            "field": "members.full_name",
            "description": "<p>Member&#39;s full name.</p> "
          },
          {
            "group": "200",
            "type": "String",
            "optional": false,
            "field": "members.short_name",
            "description": "<p>Member&#39;s short name.</p> "
          },
          {
            "group": "200",
            "type": "Date",
            "optional": false,
            "field": "members.dob",
            "description": "<p>Member&#39;s date of birth.</p> "
          },
          {
            "group": "200",
            "type": "Date",
            "optional": false,
            "field": "members.member_sunc",
            "description": "<p>Date of becoiming a parliamnet member.</p> "
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "./src/openrada/api/http.clj",
    "groupTitle": "Members"
  }
] });