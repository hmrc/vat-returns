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

##### INVALID_VRN
* **Status**: 400

##### INVALID_PERIODKEY
* **Status**: 400

##### NOT_FOUND_VRN
* **Status**: 403

##### INVALID_IDENTIFIER
* **Status**: 403

##### INVALID_INPUTDATA
* **Status**: 403

##### NOT_FOUND
* **Status**: 404


### POST /returns/vrn/:vrn

Where:
* **vrn** is a valid VRN, for example: "444444444"

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "formBundleNumber": "123456789012"
}
```

#### Error Responses

##### INVALID_VRN
* **Status**: 400

##### INVALID_PAYLOAD
* **Status**: 400

##### INVALID_SUBMISSION
* **Status**: 400

##### INVALID_PERIODKEY
* **Status**: 400

##### DUPLICATE_SUBMISSION
* **Status**: 409

##### TAX_PERIOD_NOT_ENDED
* **Status**: 403

##### NOT_FOUND_VRN
* **Status**: 403


### POST /submission

#### Success Response

**HTTP Status**: 202

**Example HTTP Response Body**:
```
{
    "nrSubmissionId": "2dd537bc-4244-4ebf-bac9-96321be13cdc"
}
```

#### Error Responses

##### Bad Request
* **Status**: 400

##### Unauthorised
* **Status**: 401

##### Not Found
* **Status**: 404

##### Checksum Failed
* **Status**: 419




### Requirements
This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE](https://www.java.com/en/download/) to run.

## License
This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
