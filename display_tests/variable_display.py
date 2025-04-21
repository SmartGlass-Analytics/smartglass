import asyncio
from frame_ble import FrameBle

async def main():
    frame = FrameBle()
    message = "Hello, Coach"

    try:
        await frame.connect()

        lua_message = message.replace("'", "\\'")
        lua_statement = f"frame.display.text('{lua_message}', 1, 1);frame.display.show();print(0)"
        # Print "Hello, Frame!" on the Frame display
        # wait for a printed string to come back from Frame to ensure the Lua has executed, not just that the command was sent successfully
        await frame.send_lua(lua_statement, await_print=True)
        print("Message sent")

        await frame.disconnect()

    except Exception as e:
        print(f"Not connected to Frame: {e}")
        return

if __name__ == "__main__":
    asyncio.run(main())