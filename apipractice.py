# necessary imports to run code
import requests

# provided by alabama mens basketball team for API access
CLIENT_ID = "client.basketball.alabamambb"
CLIENT_SECRET = "0vBg4oX7mqNx"
TOKEN_URL = "https://auth.synergysportstech.com/connect/token"

# Files and locations where we store access changing data
TOKEN_FILE = "SynergyAuth.txt"
GAME_ID = "1584413"

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

def retreive_game_stat(game_id, access_token):
    #gameStats = requests.get(url="https://basketball.synergysportstech.com/external/api/games/1584413/boxscores", auth=(client_id, client_secret))
    url = "https://basketball.synergysportstech.com/external/api/games/1584413/boxscores"
    gameStats = requests.get(url,headers={'Content-Type':'application/json','Authorization': 'token {}'.format(access_token)})
    print(gameStats)
# access_token = get_access_token(TOKEN_URL, CLIENT_ID, CLIENT_SECRET) # retreive access token for our new session
access_token = get_access_token(TOKEN_URL, CLIENT_ID, CLIENT_SECRET) # retreive access token for our new session
retreive_game_stat(100, access_token)