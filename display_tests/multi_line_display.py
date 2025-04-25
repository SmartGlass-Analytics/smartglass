import asyncio
from frame_ble import FrameBle

async def main():
    frame = FrameBle()

    # Your lines of text
    lines = [
        "Hello, Frame!",
        "Line 2: Info",
        "Line 3: More text"
    ]

    try:
        await frame.connect()

        lua_lines = []
        line_spacing = 60  # approx. pixel height of default font
        for i, line in enumerate(lines):
            safe_line = line.replace("'", "\\'")
            y_pos = 1 + i * line_spacing
            lua_lines.append(f"frame.display.text('{safe_line}', 1, {y_pos})")

        lua_code = ";".join(lua_lines) + ";frame.display.show();print(0)"
        await frame.send_lua(lua_code, await_print=True)

        print("Multiple lines sent")
        await frame.disconnect()

    except Exception as e:
        print(f"Not connected to Frame: {e}")

if __name__ == "__main__":
    asyncio.run(main())
