# Classic Den
> Project submitted to the [Microsoft Azure Trial Hackathon](https://dev.to/devteam/hack-the-microsoft-azure-trial-on-dev-2ne5). Submission post on the Dev.to. 

![classic-den-demo](/assets/classic-den-demo.gif)

**[Classic Den](https://polite-dune-017443c03.1.azurestaticapps.net)** is a simple website that can be used to post any announcement, thought, idea or question. Its style is inspired by the look and feel of classic PC systems.

## About

This project was created for  [Microsoft Azure Trial Hackathon](https://dev.to/devteam/hack-the-microsoft-azure-trial-on-dev-2ne5). My main goal was to learn something about the Microsoft Azure services, like Azure Functions or Azure Static Web Apps.

My target was to create **a low-cost website** which could be used by small communities to post announcements or other information. The main assumption is such a page would have only a few write operations (adding posts), but lots of reads (viewing page with posts).

As a result *Classic Den* is made of three components:

* frontend - a simple, framework-less application, written with plain HTML, CSS, and JS. It's served from [Azure Static Web App](https://azure.microsoft.com/en-us/services/app-service/static/) service.
* backend functions - two Java functions deployed on [Azure Functions](https://azure.microsoft.com/en-us/services/functions/) responsible for adding and removing old posts (after 14 days).
* this GitHub repository - acts as a source of code for both frontend and backend, but also it's used to store posts. Whenever a new post is created a Java function is committing a change to this repo which triggers a GitHub Action which updates static web app with a new

### Components

Here is a diagram of this solution:

![classic-den-diagram](/assets/classic-den-diagram.png)

#### Frontend (Azure Static Web App)

The frontend part is built with vanilla HTML, CSS, and JS. No framework was used. 

There are only two external dependencies used:

* [BOOTSTRA.386 project](https://github.com/kristopolous/BOOTSTRA.386) -  it is a JS and CSS theme inspired by the 1980s DOS. To learn more about it check its official GitHub project page or [the documentation with a full demo](https://kristopolous.github.io/BOOTSTRA.386/). If you, like me, are amazed at how it looks please support its creator.
* [jQuery](https://jquery.com) - a very popular library, in this project used to make calls to the Azure Function.

#### Backend (Azure Functions)

Simple plain Java application with two functions:

* create new post - an HTTP triggered function used to add a new entry by translating the JSON request into HTML post representation and committing it to this repository (`entries.html`).
* remove old posts - a time-triggered function, invoked once per day, which checks for posts that were published more than 14 days from the day there were created.

It's deployed to the Azure Functions via VS Code plugin.

#### GitHub repository

Source code of frontend and backend services. Also, it stores all posts as HTML representation (in the `entries.html` file). 

Whenever a new entry is added a new GitHub Action is triggered and it deploys all static resources to the Azure Static Web App service. Only after that, an entry will be visible on a website (that's why you need to wait a couple of minutes to see it on a page).

### Screenshots

![den1](/assets/den1.PNG)
![den2](/assets/den2.PNG)
![den3](/assets/den3.PNG)
![den4](/assets/den4.PNG)
![den5](/assets/den5.PNG)
