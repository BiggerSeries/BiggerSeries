# 1.16.4-0.3.1
 - config system boolean handling

# 1.16.4-0.3.0
 - blockstate reduction via more fine grained control
 - more descriptive blockstates for rectangular multiblock locations
 - generic multiblock controller decoupled from rectangular completely
 - patch chunkloading bug (better fix does need to be found still)
 - new gui system
 - new config system
 - toggle oregen properly
 - failed registry class loading shouldn't crash anymore (exception is caught now)
 - ticking is done from end of world tick
 - 1.16.4

# 1.16.3-0.2.1
 - Remove paused multiblock errors, don't break it
 - Fix paused multiblock orphaning detaching incorrectly

# 1.16.3-0.2.0
 - Connecting, textures, you are welcome
 - use constant vectors where applicable
 - fix NPE when loading a multiblock that hasn't yet been assembled
 - change multiblock ticks to the world tick events instead of server tick event

# 1.16.3-0.1.1
 - null the tile's caches NBT after it attaches to a controller, should reduce memory usage
 - add models for black holes and white holes
 - fix NPE when saving a multiblock that hasn't yet been assembled

# 1.16.3-0.1.0
 - Rework of multiblock NBT system, breaks mod backwards compatibility, worlds should be ok
 - Other misc breaking changes to multiblock system

# 1.16.3-0.0.5
 - Misc NBT save fixes, reactors were voiding fuel sometimes, turbines were staying at speed when disassembled, should be fixed, but its hard to test when it srandom
 - Fixed renderer stall when updating large multiblock blockstates
 - Fixed NPE with some rectangular multiblock configurations hitting an empty/null chunk section

# 1.16.3-0.0.4
 - Fix saving issues with dedicated servers
 - updated zh_cn translation

# 1.16.3-0.0.3
 - fix java 9+ compatibility

# 1.16.3-0.0.2
 - alphabetically sort creative menu
 - add zh_cn translation (thanks qsefthuopq)
 - remove ROTN
 - remove baked model support from multiblock API

# 1.16.3-0.0.1
 - added deps package as ignored package in the registry for soft dependencies

# 1.16.3-0.0.0
 - first separated release

what changes did you expect, there is nothing to change before here