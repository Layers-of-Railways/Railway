
<h1 align="center">Create Steam 'n Rails</h1>
<br><br>
<img src="https://discordapp.com/api/guilds/706277846389227612/widget.png?style=banner3" alt="Discord Banner 3" align="right"/>

**Create Steam 'n Rails** is an addon mod for Create that aims to extend Create's train and steam systems. Current features include custom tracks, semaphores, and conductors.

## Contributing (for team members):
1. Create a new branch for your feature (named `1.18/<feat>`)
2. Write your feature
3. Make a pull request
4. Have somebody review it, and merge

## Contributing (for community members):
1. Open an issue clearly describing the feature you want to implement
2. Don't start writing your feature until a core dev recommends you to
3. Fork the repository (if you haven't already), and make a branch for your feature
4. Happy coding!
5. Open a pull request, preferably linking to your issue - include your Discord username, and those of the people who helped you, so you can get the contributor role
6. (For core devs) at least 2 core devs should approve before merging - probably have discussion in Discord first as well.

### Datagen (if runData fails):
Can occasionally have some bugs, see [here](src/main/java/com/railwayteam/railways/mixin/README.md) for more info. (There should be an upstream Create fix for this, but that is not yet in any 1.18 release, and so we can't take advantage of it.)

### Commit Tricks:

- Include `[ci skip]` in your commit message to skip the automatic preview build
you can use this for example if the change you made is very minor, and not worth
a preview, or if you are just fixing a typo in the README, etc.
