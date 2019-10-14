### Main flow
- Initially list of images are displayed
- When an item is selected from the the list, selected images is displayed.
- When back pressed list is displayed again.

### Architecture
Application architecture follows MVVM pattern. Data binding is not used since it is not much needed for UI consisting of images.

### Main components
- App/AppComponent: Serves the dependency graph consisting of View, ViewModel and Model modules.
- View module presents content provided by ViewModel. Consists of MainActivity
- ViewModel provides the application behavior. Contains MainViewModel
- Model serves data to view model. Contains Photo repository: Consumes list of photos from the API

### Testing
Unit tests for MainViewModel is added. There is also a basic UI test (SmokeTest).

----
### Notes
- I am happy with architectural approach and concurrency mechanisms applied.
- If I had more time I would love to add some animations to selected item. Also I would like spend more time on UI tests.

Thank you for taking the time to review my project.

_Ugur Ozmen_