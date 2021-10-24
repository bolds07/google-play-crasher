# Bypassing Google Play policies/code inspections

## For those who have apps published at Google Play store


When you publish a new realease of your app (no matter what track, even internal test ones) Google Play automaticly run a series of tests and inspections in your code. These inspections intent to stop distribution of Malicious and Non-Compliant apps, but sometimes, they might signalize legitimate apps and abort releases. In these cases you will get an automatic e-mail from Google Play saying that your latests release was rejected due some reason

![image](https://user-images.githubusercontent.com/19866062/138605289-63926f54-8ed1-480e-8060-938b281d9ab1.png)


I have an app published on google play, which allows anyone to make a giveaway and raffle some prize among his followers.  
1 - The app is free
2 - I fully disclose that I have no relation to any social network company, influencer, brand or whatsoever
3 - All the app does is to generate a random number, place some fun animation with the followers of the user and select a winner

But still countless times I got my releases rejected due some sort of automatic inspection which classified my app as gambling, then I would change some strings in the resources submit a complain, submit another release and probably get it accepted, but until then I lost many days which can be critical if I had a major bug in the app.

Because of that, I decided to try something inusitate, I create a small code which will detect if the app being executed as an automatic test from Google Play and case positive force the app to crash.  

After this, not only I stoped getting any rejection e-mail at all, but I noticed a reduction in the delay in the time between I releaed the app to production and it became available to the final users.

In some tests I made, apps relased to production without the Automatic Inspections Crash Code would take around 1 week to be delivered to google play, while apps running my detection test would take FEW HOURS

# Disclaimer

* I have never worked with Google, Google Play or Firebase
* I do not have access to the workflow which Google Play uses to approve/reject an apps release, all what I did was using empirical research
* IF YOU USE FIREBASE **TEST LAB** THIS CODE WILL AFFECT YOUR TESTS
