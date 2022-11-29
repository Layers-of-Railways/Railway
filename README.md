<h1 align="center">Steam n' Rails <br>
<a href="https://discord.com/invite/gcgfkca4rq"><img src="https://img.shields.io/discord/929394649884405761?color=5865f2&label=Discord&style=flat" alt="Discord"></a>

**Create Steam 'n Rails** is an addon mod for Create that aims to extend Create's train and steam systems. Current features include custom tracks, semaphores, and conductors.

## Contributing (for team members):
1. Create a new branch for your feature (named `1.18/<feat>`)
2. Write your feature
3. Make a pull request
4. Have somebody review it, and merge

### Datagen (if runData fails):
Can occasionally have some bugs, see [here](src/main/java/com/railwayteam/railways/mixin/README.md) for more info. (There should be an upstream Create fix for this, but that is not yet in any 1.18 release, and so we can't take advantage of it.)

### Commit Tricks:
- Include `ci skip` in your commit message to skip the automatic preview build
you can use this for example if the change you made is very minor, and not worth
a preview, or if you are just fixing a typo in the README, etc.
