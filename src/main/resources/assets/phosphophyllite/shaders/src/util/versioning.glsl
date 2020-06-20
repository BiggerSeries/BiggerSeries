#pragma once

#ifdef VERSION_450

#define VERSION_450_DEFINE #version 450
VERSION_450_DEFINE

#define GL_ARB_separate_shader_objects_DEFINE #extension GL_ARB_separate_shader_objects : enable
GL_ARB_separate_shader_objects_DEFINE

#define VERSION_450_OR_GREATER
#define VERSION_330_OR_GREATER

#elif defined(VERSION_330)

#define VERSION_330_DEFINE #version 330
VERSION_330_DEFINE

#define GL_ARB_explicit_uniform_location_DEFINE #extension GL_ARB_explicit_uniform_location : enable
GL_ARB_explicit_uniform_location_DEFINE

#define VERSION_330_OR_GREATER

#elif defined(VERSION_120)

#define VERSION_120_DEFINE #version 120
VERSION_120_DEFINE

#define GL_EXT_texture_integer_DEFINE #extension GL_EXT_texture_integer : enable
GL_EXT_texture_integer_DEFINE

#else

#error UNSUPPORTED_VERSION

#endif