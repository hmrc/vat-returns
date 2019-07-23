# VAT Returns Microservice

[![Build Status](https://travis-ci.org/hmrc/vat-returns.svg)](https://travis-ci.org/hmrc/vat-returns)
[![Download](https://api.bintray.com/packages/hmrc/releases/vat-returns/images/download.svg) ](https://bintray.com/hmrc/releases/vat-returns/_latestVersion)

## Summary
This protected microservice provides a backend for Making Tax Digital for Businesses (MTDfB) VAT frontend services to retrieve VAT Returns for a user enrolled for MTD VAT.

## Running the application
To run this microservice, you must have SBT installed. You should then be able to start the application using:

```sbt run``` 

### Testing
```sbt test it:test```


## Endpoints

### GET /returns/vrn/{vrn}?period-key={periodkey}

Where:
* **vrn** is a valid VRN, for example: "444444444"
* **periodkey** is the period key of the return, for example: "18AA"

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "periodKey": "18AA",
   "vatDueSales": 100.00,
   "vatDueAcquisitions": 300.00,
   "vatDueTotal": 400.00,
   "vatReclaimedCurrPeriod": 150.00,
   "vatDueNet": 250.00,
   "totalValueSalesExVAT": 100.00,
   "totalValuePurchasesExVAT": 100.00,
   "totalValueGoodsSuppliedExVAT": 100.00,
   "totalAllAcquisitionsExVAT": 100.00,
   "agentReferenceNumber": "XAIT0000000000"
}
```
Where:
* **agentReferenceNumber** is an optional field

#### Error Responses

Status | Error codes
:---:|:---:
400    |INVALID_VRN, INVALID_PERIODKEY
403    |NOT_FOUND_VRN, INVALID_IDENTIFIER, INVALID_INPUTDATA
404    |NOT_FOUND


### POST /returns/vrn/:vrn

Where:
* **vrn** is a valid VRN, for example: "444444444"

#### Example Request
```
{
   "periodKey": "18AA",
   "vatDueSales": 100.00,
   "vatDueAcquisitions": 300.00,
   "vatDueTotal": 400.00,
   "vatReclaimedCurrPeriod": 150.00,
   "vatDueNet": 250.00,
   "totalValueSalesExVAT": 100.00,
   "totalValuePurchasesExVAT": 100.00,
   "totalValueGoodsSuppliedExVAT": 100.00,
   "totalAllAcquisitionsExVAT": 100.00,
   "agentReferenceNumber": "XAIT0000000000"
}
```
Where:
* **agentReferenceNumber** is an optional field


#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "formBundleNumber": "123456789012"
}
```

#### Error Responses

Status | Error codes
:---:|:---:
400    |INVALID_VRN, INVALID_PAYLOAD, INVALID_SUBMISSION, INVALID_PERIODKEY
403    |TAX_PERIOD_NOT_ENDED
409    |DUPLICATE_SUBMISSION

### POST /submission

#### Example Request
```
{
    "payload": "Zm9vYmFyMTIzCg==",
    "metadata": {
        "businessId": "vat",
        "notableEvent": "vat-return",
        "payloadContentType": "text/html",
        "payloadSha256Checksum": "426a1c28<snip>d6d363",
        "nrSubmissionId": "2dd537bc-4244-4ebf-bac9-96321be13cdc",
        "userSubmissionTimestamp": "2018-04-07T12:13:00.250Z",
        "identityData": {
            "internalId": "some-id",
            "externalId": "some-id",
            "agentCode": "TZRXXV",
            "credentials": {
                "providerId": "12345-credId",
                "providerType": "GovernmmentGateway"
            },
            "confidenceLevel": 200,
            "nino": "DH00475D",
            "saUtr": "Utr",
            "name": {
                "name": "test",
                "lastName": "test"
            },
            "dateOfBirth": "1985-01-01",
            "email":"test@test.com",
            "agentInformation": {
                "agentCode" : "TZRXXV",
                "agentFriendlyName" : "Bodgitt & Legget LLP",
                "agentId": "BDGL"
            },
            "groupIdentifier" : "GroupId",
            "credentialRole": "admin",
            "mdtpInformation" : {
                "deviceId" : "DeviceId",
                "sessionId": "SessionId"
            },
            "itmpName" : {
                "givenName": "test",
                "middleName": "test",
                "familyName": "test"
            },
            "itmpDateOfBirth" : "1985-01-01",
            "itmpAddress" : {
                "line1": "Line 1",
                "postCode": "NW94HD",
                "countryName": "United Kingdom",
                "countryCode": "UK"
            },
            "affinityGroup": "Agent",
            "credentialStrength": "strong",
            "loginTimes": {
                "currentLogin": "2016-11-27T09:00:00.000Z",
                "previousLogin": "2016-11-01T12:00:00.000Z"
            }
        },
        "userAuthToken": "Bearer AbCdEf123456...",
        "headerData": {
                        "Gov-Client-Public-IP": "198.51.100.0",
                        "Gov-Client-Public-Port": "12345",
                        "Gov-Client-Device-ID": "beec798b-b366-47fa-b1f8-92cede14a1ce",
                        "Gov-Client-User-ID": "alice_desktop",
                        "Gov-Client-Timezone": "GMT+3",
                        "Gov-Client-Local-IP": "10.1.2.3",
                        "Gov-Client-Screen-Resolution": "1920x1080",
                        "Gov-Client-Window-Size": "1256x803",
                        "Gov-Client-Colour-Depth": "24",
                       
                        ...
                      },
        "searchKeys": {
                        "vrn": "123456789",
                        "periodKey": "A1"
                      },
        "receiptData": {
            "language": "en",
            "checkYourAnswersSections": {
                "title": "VAT details",
                "data": [
                    {
                        "questionId": "VatDetails1",
                        "question": "VAT taxable sales this quarter",
                        "answer": "£1,392,483.20"
                    },
                    {
                        "questionId": "VatDetails2",
                        "question": "VAT start date",
                        "answer": "20 July 2016"
                    }
                ]
            },
            "declaration": {
                "declarationText": "I confirm the data..."
                "declarationName": "John Smith",
                "declarationRole": "Finance Director",
                "declarationConsent": true
            }
        }
    }
}
```


#### Success Response

**HTTP Status**: 202

**Example HTTP Response Body**:
```
{
    "nrSubmissionId": "2dd537bc-4244-4ebf-bac9-96321be13cdc"
}
```

#### Error Responses

Status | Error
:---:|:---:
400    |Bad Request
401    |Unauthorised
404    |Not Found
419    |Checksum Failed



### Requirements
This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE](https://www.java.com/en/download/) to run.

## License
This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
