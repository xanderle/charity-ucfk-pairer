function geocodeSelectedCells() {
  var sheet = SpreadsheetApp.getActiveSheet();
  var cells = sheet.getActiveRange();

  // Must have selected 3 columns (Location, Lat, Lng).
  // Must have selected at least 1 row.

  if (cells.getNumColumns() != 3) {
    Logger.log("Must select the Location, Lat, Lng columns.");
    return;
  }

  var addressColumn = 1;
  var addressRow;

  var latColumn = addressColumn + 1;
  var lngColumn = addressColumn + 2;

  var geocoder = Maps.newGeocoder().setRegion('de');
  var location;

  for (addressRow = 1; addressRow <= cells.getNumRows(); ++addressRow) {
    address = cells.getCell(addressRow, addressColumn).getValue();

    // Geocode the address and plug the lat, lng pair into the
    // 2nd and 3rd elements of the current range row.
    location = geocoder.geocode(address);

    // Only change cells if geocoder seems to have gotten a
    // valid response.
    if (location.status == 'OK') {
      lat = location["results"][0]["geometry"]["location"]["lat"];
      lng = location["results"][0]["geometry"]["location"]["lng"];

      cells.getCell(addressRow, latColumn).setValue(lat);
      cells.getCell(addressRow, lngColumn).setValue(lng);
    }
  }
};

/**
 * Adds a custom menu to the active spreadsheet, containing a single menu item.
 *
 * The onOpen() function, when defined, is automatically invoked whenever the
 * spreadsheet is opened.
 *
 * For more information on using the Spreadsheet API, see
 * https://developers.google.com/apps-script/service_spreadsheet
 */
function onOpen() {
  var sheet = SpreadsheetApp.getActiveSpreadsheet();
  var entries = [{
    name: "Geocode Selected Cells",
    functionName: "geocodeSelectedCells"
  }];
  sheet.addMenu("Macros", entries);
};
