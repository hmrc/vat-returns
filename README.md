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
