# Faris-Custom-Image-Viewer
Simple but useful image viewer (made with java).

#### Downloads:
- [Version 1.3 Download](http://www.filehosting.org/file/download/716023/XyO61lXGXBiNBkLO).

# Why make an image viewer?
The reason is because I hate the windows image viewer, it has problems. If Microsoft isn't gonna fix them, then I can make my own image viewer.

# Basic Idea
When you open a file with this program, a list is created and the file is added. All the other files under the same directory will be added too. And my favorite part: files inside sibbling directories will be traversed and added as well!

# Current Features
- Traverse all files and directories under the same directory of the current image file.
- Drag and drop files or folders on to the image viewer window to add them to the list.
- Show the current image list loaded in the program, and clicking on the item will show it in the system explorer.




# Features to add
- #### Scroll/Zoom:
	- [ ] Zoom toward mouse
	- [ ] Fix scroll out problem (when zooming out and the pic gets too small, it gets stuck, i.e no more zoom control)
	- [ ] Add an option for mousewheel: Next/previous image, or Zoom in and out
Thumbnail view
- #### Drag and drop:
	- [ ] Add support to start from a Directory
	- [ ] Make sure that images are in correct order upon loading, and the opened image is what's shown.
	 
- #### GIFs:
	- [ ] Fix playback speed
	- [ ] Fix blank screen on GIFs: (sometimes if a gif takes too long to load, it will stay all blank, until navigating away then back)
	- [ ] Fix properties label problem with gifs
	- [ ] Start GIF from beginning when navigating to it (current problem: GIF is always playing even when not open).
	- [ ] Show GIF timeline
Fix deletion problem
- #### ImageList:
	- [ ] Put the image count in the title
	- [ ] Align imageList text to left (maybe add an icon too :D)
	- [ ] Add option for what clicking the image list does, either shows in explorer or navigates to it (or something else, whatever).
- [ ] Remember zoom level when navigating between images.
- [ ] Have a buffer load list for the next and previous 10 pics.
- [ ] Add a custom sort menu to sort by (Dimension, type, …)
- [ ] HOME/END hotkeys will go to the first/last pictures.

### Less significant
- [ ] Remake prettier GUI (or get someone to do it)
- [ ] Add feature to reverse image saerch pic (using Google or Tineye or whatever).


