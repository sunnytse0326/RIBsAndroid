# RIBs Proof Of Concept Work - Android

RIBs is an architecture which controls by independent business logics and managable by couples of developers. From this example, we are giving an demonstration on how to build up a simple chating application by using [Sendbird Library](https://sendbird.com/)


# Project Architechure
In the project, we buit several RIBs components to seperate the jobs and divided into modules.
<br>
<br>
The diagram beneath shows the flow how it works in the project:
<p float="left">
  <img src="https://github.com/sunnytse0326/RIBsAndroid/blob/master/screenshot/structure.png" width="563" height="408">
</p>
<br>
Each RIB tree has its own functionality:
<br>
Root: First RIB tree in application, a place for some data initialization
<br>
LoggedIn: Responsible for handling tokens and login functions from Sendbird library
<br>
Chatlist: Handle chat channel and display message list
<br>
TypeText: A hint text when user is typing
<br>

# Implementation
1. We need to install [Android Studio](https://developer.android.com/studio/) and download all neccessary SDK tools for development. [Here](https://developer.android.com/training/basics/firstapp/creating-project) will show you how to build up a normal example project.


2. For RIBs plugin, please install [RIBs Plugin](https://github.com/uber/RIBs/wiki/Android-Tooling#ribs-code-generation-plugin-for-android-studio-and-intellij) and follow the steps provided if you would like to generate a RIB tree file from project.


3. From the project, it sets two kind of build flavor for different users. Each provides a login credit and build a chat room from SendBird:
<p float="left">
  <img src="https://github.com/sunnytse0326/RIBsAndroid/blob/master/screenshot/screenshot1.png" width="271" height="451">
  <img src="https://github.com/sunnytse0326/RIBsAndroid/blob/master/screenshot/screenshot2.png" width="271" height="451">
</p>

