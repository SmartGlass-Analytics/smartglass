import frame_sdk

def trythis():
    frame = frame_sdk.Frame("DC:3A:AF:19:9D:35")
    # Send a Lua command
    lua_code = 'print("Hello from Frame!")'
    frame.send_lua(lua_code)
    # Optionally, wait for a response
    response = frame.await_print(timeout=5)
    print(response)