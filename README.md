# FinalProject_Bootcamp

Website Homepage
![Image text](https://github.com/kitt9856/FinalProject_Bootcamp/blob/main/img/FinalPjHomepage.jpg)

## Backend Verison Update

In 0.0.1-SNAPSHOT ver., "Open Price" per minute was fixed it leads to  Candlestick Chart display the open price at a static point and it appears illogical

In 0.0.2-SNAPSHOT ver., Add a extra api data source try to sovle the issue of 0.0.1-SNAPSHOT ver.

# Features

## Backend
* Real-time stock data soucre fetching from Yahoo Finance API
 * Market price updates per minute, reflecting price changes in real time
* Historical stock data (From today up to the past 3 month)
* RESTful API endpoints for frontend (Mainly GET requests)
 * Includes stock name search functionality
* Using Redis to efficiently handle data interactions
* Native SQL logic to optimize search functionality with Frontend

## Frontend
* Focused on data visualization
* Real-time trading representation using both line charts and candlestick charts
* Responsive Design


## Tech Stack
Backend: Java (Spring Boot)
Frontend: Java (thymeleaf), HTML, CSS, JavaScript
Database: PostgreSQL
Cache Management: Redis
Containerization: Docker (Deployment)

## Project Structure
FinalProject_Bootcamp-main/
├── Backend/
│   └── crumbcookieresponse/
│       ├── src/main/java/com/crumbcookie/crumbcookieresponse/
│       │   ├── Appcofig/
│       │   ├── Controller/
│       │   ├── dto/
│       │   ├── Entity/
│       │   ├── lib/
│       │   ├── model/
│       │   ├── Repository/
│       │   ├── Service/
│       │   └── CrumbcookieresponseApplication.java
│       ├── src/main/resources/
│       ├── Dockerfile
│       └── pom.xml
├── Frontend/
│   └── bc-xfin-web/
│       ├── src/main/java/com/springfront/bc_xfin_web/
│       │   ├── Appconfig/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── lib/
│       │   ├── model/
│       │   ├── service/
│       │   └── BcXfinWebApplication.java
│       ├── src/main/resources/
│       │   ├── static/ (CSS, JS)
│       │   └── templates/ (HTML)
│       ├── Dockerfile
│       └── pom.xml
└── README.md

## Appendix
Request API if no auth
![Image text](https://github.com/kitt9856/FinalProject_Bootcamp/blob/main/img/APIunanthorError.jpg)

Get API while reset Headers -> successful 
 ![Image text](https://github.com/kitt9856/FinalProject_Bootcamp/blob/main/img/FinalPjHomepage.jpg)

Prepare Element for present data
![Image text](https://github.com/kitt9856/FinalProject_Bootcamp/blob/main/img/PrepareHTML.jpg)

Problem before Backend Verison Update
![Image text](https://github.com/kitt9856/FinalProject_Bootcamp/blob/main/img/FixRegularOpenPrice.jpg)

