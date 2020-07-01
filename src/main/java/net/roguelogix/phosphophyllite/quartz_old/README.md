# Phosphophyllite Quartz Renderer
Quartz is a render engine designed to work alongside minecraft's built in engine to help with dynamically textured blocks

If you are using this engine, it is recommend you *don't* poke around with the OpenGL state at all, 
depending on what OpenGL capabilities are available, it will switch what its doing

##Note
Everything this renderer does is strictly "on top" of what Minecraft does,
it will not remove models from the Minecraft engine, it will not alter the lighting conditions, 
and minecraft will still render a block at the same location if you ask it too