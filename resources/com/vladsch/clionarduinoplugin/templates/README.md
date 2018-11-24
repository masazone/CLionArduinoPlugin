# Arduino Support Templates

This directory contains the templates used by Arduino Support plugin to populate new projects
and create new sketch files.

## Structure

Subdirectories contain files for each operation:

- `library/`, not used but intended for future `New Library` file action to add a local library
  to project.
- `project/library_arduino`, used for populating a new arduino library project
- `project/library_static`, used for populating a new static library project
- `project/sketch`, used for populating a new sketch project
- `sketch`, used to create a new sketch (will create more files if they are added to this
  directory)

Subdirectories under the directories for each action will also be created and populated with
files they contain, provided the files are not completely empty and do not have
[inherited content](#inherited-file-content).

:warning: If you add files under the `sketch` directory, these files will be created every time
you invoke the `New Sketch` action. The file names for new sketch action should be based on the
`@sketch_name@` to make them unique, however they can also use the `@project_name@` variable
(and its variations) if desired.

## Inherited File Content

Since many files will have the same content across action types, empty files can be used as
placeholders in the directory, with the content for them provided in an upper level directory.

Files which are empty, will have their content inherited from matching files higher up in the
directory hierarchy. Note that this matching process occurs before variable name replacement.

If a parent directory has a non-empty file whose name and extension match then its content will
be used for the file. Failing that, if there is a non-empty file with `@@` name and the same
extension as the empty file then its content will be used.

This process goes all the way up to the parent directory of all templates.

## File Name and Text Replacement

File names are mangled to have the name replaced with creation time variables. The text @text@
defines a variable which may be available during creation. If the variable is not available it
will evaluate to empty and if this results in an empty file name then that file will not be
created.

All variables in file names are between `@` and `@`, in file content between `<@` and `@>`

Additionally, some variables are made available in all caps `ALL_CAPS`, camel case `camelCase`,
pascal case `PascalCase` or snake case `snake_case` variations. The case style of the variable
name reflects the style of its value.

Not all operations have all variables defined.

- `sketch_name`, name provided to new sketch action, and only available in this action.
- `LIBRARY_NAME`, name of the library project (variations: `library_name`, `libraryName`,
  `LibraryName`) available on new library project action.
- `PROJECT_NAME`, name of the project (variations: `project_name`, `projectName`, `ProjectName`)
  available on all new project and file actions.

These variables are available on new project actions and reflect the selection of settings in
the new project wizard:

- `USER_NAME`, author name provided for the arduino library project
- `E_MAIL`, e-mail provided for the arduino library project
- `LIBRARY_CATEGORY`, library category provided for the arduino library project
- `LIBRARY_DISPLAY_NAME`, library name (used for `library.properties` file) provided for the
  arduino library project

## Empty Variable in Content

To handle a special case when a variable is not defined at file creation but would leave an
invalid file, a `<@DELETE_IF_BLANK[<@variable-name@>]@>` is used. It has the effect of deleting
the line in which it is contained if the text between `[]` evaluates to empty. The result of
expanding this directive is always empty so it leaves no text in the file.

This is used in the `@@.ino` file at the root of templates to include the library header, which
is not defined in non-library projects. The line:

    #include "<@library_name@>.h"<@DELETE_IF_BLANK[<@library_name@>]@>

Will either evaluate to include the library header or be deleted during variable replacement.

## CMakeLists.txt File

This file reflects the template used for creating new projects and is code dependent.

Deleting lines in this file will have no effect because on project creation they will be added
by the code. The order of the lines may change since the code does not have an existing location
to modify the command and will add it based on its built-in layout table.

The exception to this are comment lines which if removed will not be available in new projects'
files.

