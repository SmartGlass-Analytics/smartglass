# necessary imports to run code
import requests

# provided by alabama mens basketball team for API access
CLIENT_ID = "client.basketball.alabamambb"
CLIENT_SECRET = "0vBg4oX7mqNx"
TOKEN_URL = "https://auth.synergysportstech.com/connect/token"

# Files and locations where we store access data
TOKEN_FILE = "SynergyAuth.txt"

#How to obtain access token from Synergy using OAuth2.0
def get_access_token(url, client_id, client_secret):
    # make the request and save our two important values
    response = requests.post(
        url,
        data={"grant_type": "client_credentials"},
        auth=(client_id, client_secret),
    )
    token = response.json()["access_token"] # token itself
    duration = response.json()["expires_in"] # saving when token expires for reminders later
    
    # write to a text file and store our newly created token
    with open(TOKEN_FILE, "w") as file:
        file.write(f"Warning: Token expires in {duration} seconds.")
        file.write("\n-----------Token Below-----------\n")
        file.write(token)
    
    return response.json()["access_token"] # return token itself for use in code


access_token = get_access_token(TOKEN_URL, CLIENT_ID, CLIENT_SECRET) # retreive access token for our new session