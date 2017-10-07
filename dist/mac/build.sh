#!/bin/bash

title=$1
finalDMGName=$3
targetdir=$4
srcdir=$5

# Ensure the app direcotry has suitable permissions
SetFile -a B ${srcdir}

# Clean up any old builds and copy over app dir
rm -rf ${targetdir}/dmg && mkdir -p ${targetdir}/dmg
rm -f ${targetdir}/${finalDMGName}
cp -r ${srcdir} ${targetdir}/dmg

# Ensure volume is not mounted before we start
umount -f /Volumes/"${title}"

# Create the DMG in read / write mode
hdiutil create -volname ${title} -size 500m -srcfolder ${targetdir}/dmg/ -ov -format UDRW ${targetdir}/pack.temp.dmg

# Mount the DMG
device=$(hdiutil attach -readwrite -noverify -noautoopen "${targetdir}/pack.temp.dmg" | egrep '^/dev/' | sed 1q | awk '{print $1}')

sleep 5


# Add extra stuff to the installer so it looks nice
echo '
   tell application "Finder"
     tell disk "'${title}'"
           open
           set current view of container window to icon view
           set toolbar visible of container window to false
           set statusbar visible of container window to false
           set the bounds of container window to {400, 100, 885, 430}
           set theViewOptions to the icon view options of container window
           set arrangement of theViewOptions to not arranged
           set icon size of theViewOptions to 72
           make new alias file at container window to POSIX file "/Applications" with properties {name:"Applications"}
           set position of item "Spectre" of container window to {100, 100}
           set position of item "Applications" of container window to {375, 100}
           update without registering applications
           delay 5
           close
     end tell
   end tell
' | osascript

# Unmount the DMG
hdiutil detach ${device}

# Convert the DMG to read only zipped version
hdiutil convert ${targetdir}/pack.temp.dmg -format UDZO -imagekey zlib-level=9 -o ${targetdir}/${finalDMGName}

# Delete the now redundant R/W DMG version
rm -f ${targetdir}/pack.temp.dmg
