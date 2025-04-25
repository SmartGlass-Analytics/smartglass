import asyncio
from frame_ble import FrameBle

async def main():
    frame = FrameBle()

    # Define each line as a separate variable
    line_1 = "Mark Sears:"
    line_2 = "24pts"
    line_3 = "8reb"
    line_4 = "7ast"

    lines = [line_1, line_2, line_3, line_4]

    try:
        await frame.connect()

        line_spacing = 60
        start_y = 1

        # Set font first
        await frame.send_lua("frame.display.set_font('default')")

        # Loop through each line and send to the Frame
        for i, line in enumerate(lines):
            safe_line = line.replace("'", "\\'")  # Escape any single quotes in text
            y = start_y + i * line_spacing
            lua_line = f"frame.display.text('{safe_line}', 1, {y})"
            await frame.send_lua(lua_line)  # Send each line separately

        # Finalize the display
        await frame.send_lua("frame.display.show();print(0)", await_print=True)

        print("All lines sent to Frame.")
        await frame.disconnect()

    except Exception as e:
        print(f"Not connected to Frame: {e}")

if __name__ == "__main__":
    asyncio.run(main())
