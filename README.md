# FyberChallenge

###Action Steps
1. Create a form asking for the variable params (uid, API Key, appid, pub0)
2. Make the request to the API passing the params and the authentication hash
3. Get the result from the response.
4. Check the returned hash to check that it’s a real response (check signature). You can find signature validation in WebBinder.java
5. Render the offers in a view.
A. If we have offers there we render them (title, teaser, thumbnail hires and
payout)
B. If we have no offers there we render a message like ‘No offers’
6. Create functional and unit tests
