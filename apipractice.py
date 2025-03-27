# necessary imports to run code
import requests
import json

# provided by alabama mens basketball team for API access
CLIENT_ID = "client.basketball.alabamambb"
CLIENT_SECRET = "0vBg4oX7mqNx"
TOKEN_URL = "https://auth.synergysportstech.com/connect/token"

# Files and locations where we store access changing data
TOKEN_FILE = "SynergyAuth.txt"
BOX_SCORE_JSON = "BoxScore.json"
BOX_SCORE_DATA = "BoxScore.txt"
GAME_ID = "67c7597c0d7d2e7999594652"

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
    print(response.json())
    return response.json()["access_token"] # return token itself for use in code

def printPlayerStats(boxJS):
    with open(BOX_SCORE_DATA, "w") as file:
        teamOne = boxJS[0]["data"]["team"]["fullName"]
        switchedTeam = False
        file.write(teamOne + "\n")
        for entry in boxJS:
            data = entry["data"]
            if data["team"]["fullName"] != teamOne and not switchedTeam:
                file.write("\n" + data["team"]["fullName"] + "\n")
                switchedTeam = True
            playerName = json.dumps(data["player"]["name"]).strip('"')
            points = json.dumps(data["points"])
            oreb = json.dumps(data["offReb"])
            dreb = json.dumps(data["defReb"])
            assists = json.dumps(data["assists"])
            resultString = f"{playerName}: {points} PTS, {assists} AST, {oreb} OREB, {dreb} DREB"
            if "ppp" in data.keys(): 
                ppp = json.dumps(data["ppp"]) 
                resultString += f", {ppp[0:5]} PPP"
            file.write(resultString +"\n")


def retreive_game_stat(game_id, access_token):
    url = f"http://basketball.synergysportstech.com/external/api/games/{game_id}/boxscores"
    gameStats = requests.get(url,headers={'Authorization': f'Bearer {access_token}'})
    boxJS = gameStats.json()["data"]
    with open(BOX_SCORE_JSON, 'w') as f:
        json.dump(boxJS, f, indent=4)
    printPlayerStats(boxJS)

access_token = get_access_token(TOKEN_URL, CLIENT_ID, CLIENT_SECRET) # retreive access token for our new session
retreive_game_stat(GAME_ID, access_token)
