------------------------------------------------------
Version 1.6.0
------------------------------------------------------
Additions
- Cherry Tracks (1.20.1)
- Bamboo Tracks (1.20.1)
- Stripped Bamboo Tracks (1.20.1)
- Handcar: wrench to pick up
- Players can now click a station with a whistle to summon a train directly to that station
- A deployer using a whistle on air will clear the schedule of the bound train
- You can now whistle a dual-headed train while in motion and have it slow and reverse
- Config option to use old-style smoke (campfire particles) for smokestacks
- Locometal block series

Changes
- Generic Crossing Tracks - any two tracks can now cross at a junction
- Decoupled trains' names will now stop at one "Split off from: ..."
- Trains will now approach nearby stations when they are the rear train in a decoupled set
- Trains relocated by a wrench will now try to approach nearby stations after relocation
- Decoupled trains will now move back and forth a little bit to prevent signal overruns
- Held conductor whistles will attempt to rebind after a conductor has moved to a new train (such as after coupling/decoupling)
- Reduce comparator output checking frequency for Track Coupler
- Couplers validate placement less frequently, improving performance

Fixes
- Mixin conflict with VS2
- Fix Farmers delight fabric crash (small hacky fix)
- Autoschedule application properly sets the schedule index on trains, increasing reliability in automated coupling systems
------------------------------------------------------
Version 1.5.1
------------------------------------------------------
Additions
- Optifine Warning Screen

Bug Fixes
- Fix NPE in ConductorCapItem
------------------------------------------------------
Version 1.5.0
------------------------------------------------------
Additions
- Smoke dying: place wool under a smokestack to change the smoke color
- Automatic whistle operation
- New dev caps for Rabbitminers and TropheusJay
- Wide gauge tracks
- Narrow gauge tracks
- Invisible Monobogeys
- (Invisible) Monobogeys can drive on phantom tracks
- Invisible (mono)bogeys can drive on any track type
- Simple Voice Chat integration for conductor spies
- Oilburner Smokestack can be encased with industrial iron
- Collision to track encasement
- Narrow and wide gauge bogeys

Changes
- Update to Create 0.5.1c
- Improved smoke from smokestacks

Bug Fixes
- Non-compat custom curved tracks render with flywheel off in production (#180)
- Disabling datafixers should work now (#186)
- Use vanilla entity data accessors, hopefully quilt clients can join fabric servers now
- Use 'proper' class transformation to add Track Replace rolling mode (#194)
- Polymer will no longer crash with Steam 'n' Rails

Optimizations
- Remove expensive call to get stack trace in performance-critical navigation code
- Optimize: Config option to disable train collision
------------------------------------------------------
Version 1.4.x
------------------------------------------------------
Additions
- SecurityCraft compat (#172)
- Added switches
- Added invisible bogeys
- Added various single, double, and triple axle bogeys
- Added Track Replace mode to rollers
- Added remote control to conductors (experimental)
- Added spy conductors
- Added vents
- Added coupler ponder

Changes
- Prevent conductors taking damage while riding trains
- Prevent conductors being knocked out of their seats

Bug Fixes
- Custom curved tracks render with flywheel off in production (#169)
- Fix mixin crash
- Fixed rollers breaking custom tracks
- Fixed display links targeting nixie tube signals
- Fixed coupler trying to couple/decouple partially loaded trains
------------------------------------------------------
Version 1.3.x
------------------------------------------------------
Additions
- Add Display Link functionality to allow remote signalling
- Add new tracks: tieless, phantom, and ender
- Add compatibility tracks for the following mods: Hex Casting, Oh The Biomes You'll Go, Blue Skies, Twilight Forest, and Biomes O' Plenty
- Add some exception handling to coupler

Changes
- Port to Create 0.5.1
- Rework train door system
- Update smokestack appearance to better stack on top of other blocks

Bug Fixes
- Fix Minecart Drops
- Fix smoke stacks
- Fix config
- Fixed Recipes
- Fixed Mining
- (1.19 only) fixed mangrove tracks
- fixed sloped tracks
------------------------------------------------------
Version 1.2.x
------------------------------------------------------
Bug Fixes
- Fix track placement issue (consuming base Create tracks instead of custom ones)
- Fix #110 (Couplers crashing when scrolled onto invalid location)