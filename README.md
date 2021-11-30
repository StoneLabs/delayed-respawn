<img alt="GitHub Workflow Status (branch)" src="https://img.shields.io/github/workflow/status/StoneLabs/delayed-respawn/build/master?label=master&style=flat-square"> <img alt="GitHub issues" src="https://img.shields.io/github/issues/StoneLabs/delayed-respawn?style=flat-square"> <img alt="Version" src="https://img.shields.io/badge/Minecraft%20Version-1.18-blue?style=flat-square"> <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/StoneLabs/delayed-respawn?style=flat-square"> <img alt="GitHub all releases" src="https://img.shields.io/github/downloads/StoneLabs/delayed-respawn/total?color=blue&label=downloads&style=flat-square">

<img src="https://user-images.githubusercontent.com/19885942/144099416-36a55ce9-36da-4b84-8be0-e00bab08678c.png" align="right" width="250" />

# Delayed Respawn

Server side mod that will deny your connection if you died within the last X hours.

<img src="https://user-images.githubusercontent.com/19885942/144099533-582db778-3265-4ac9-8e6a-1f7fb4a49822.png" align="right" width="250" />

## Details

Its quite simple. You die, you take a timeout. Semi-hardcore so to speak. These are saved in `timeouts.json` in your server directory.

Operators can pardon deaths using `/pardon-death` and `/unpardon-death`, or list them using `/timeouts`. The duration of the timeout in seconds can be adjusted using the `deathTimeout` gamerule.

## Download

[See release page.](https://github.com/StoneLabs/delayed-respawn/releases)

## License

This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org/>
