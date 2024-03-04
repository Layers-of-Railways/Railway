------------------------------------------------------
Version 1.6.0
------------------------------------------------------
Additions
- Cherry, Bamboo & Stripped Bamboo Tracks (1.20.1)
- Nature's Spirit compat Tracks (1.20.1)
- TerraFirmaCraft compat Tracks (1.20.1)
- Create: Dreams and Desires compat Tracks
- Quark compat tracks
- Handcar: wrench to pick up
- Players can now click a station with a whistle to summon a train directly to that station
- A deployer using a whistle on air will clear the schedule of the bound train
- You can now whistle a dual-headed train while in motion and have it slow and reverse
- Config option to use old-style smoke (campfire particles) for smokestacks
- Locometal block series
- Buffer blocks
- Crafting tables work on trains
- Hovering over a track-related block (couplers, signals, stations, etc.) with a wrench will highlight the track it is bound to and vice versa
- Fuel Tanks
- Fuel Interfaces
- Liquid Fuel System (liquid fuel can power trains)
- Tag to disable items from being used as train fuel

Changes
- Generic Crossing Tracks—any two tracks can now cross at a junction
- Decoupled trains' names will now stop at one "Split off from: ..."
- Trains will now approach nearby stations when they are the rear train in a decoupled set
- Trains relocated by a wrench will now try to approach nearby stations after relocation
- Decoupled trains will now move back and forth a little bit to prevent signal overruns
- Held conductor whistles will attempt to rebind after a conductor has moved to a new train (such as after coupling/decoupling)
- Reduce comparator output checking frequency for Track Coupler
- Couplers validate placement less frequently, improving performance
- Remote Conductor-controlled trains adjust speed based on signal strength
- Smoke rework 2.0: more minecraft-style smoke, with config for old smoke
- Train status messages now include coordinates
- Soul fire smoke in smoke stacks, clicking a smokestack with soul soil or soul sand will make it output soul fire themed smoke
- Smokestacks can be dyed via being clicked with dye
- Smokestacks show their dye color/style when wearing goggles
- Signals, stations, and other blocks with a 'track pad' do not render the pad on phantom tracks when the tracks are not visible
- Radiator fans can now be placed in any direction
- Creeper explosions and Ghast Fireballs no longer break tracks
- Remastered Monobogeys, Single Axle, Double Axle and Triple Axle Bogies
- Removed optifine warning screen

Fixes
- Mixin conflict with VS2
- Auto schedule application properly sets the schedule index on trains, increasing reliability in automated coupling systems
- Fix waypoint schedule items not showing up in the Station Summary (#329)
- Fix villagers opening sliding doors in the 'special' mode (#317)
- Fix normal nixie tube relays (#311)
- Fix signal debug line rendering for monorail tracks
- Change conductor cap offset on players to fix clipping with some skins
- Rework a conductor spy mixin for Tweakeroo freecam compat
- Offset train assembly overlay on encased tracks
- Made the mod work with optifine again
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