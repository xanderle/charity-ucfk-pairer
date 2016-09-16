// Class to contain representations of volunteers

public class Volunteer {

  public String name;
  public String email;
  public String phone;
  public boolean exp;
  public boolean driver;
  public String address;
  public double lat;
  public double lng;

  public Volunteer(String fullName, String emailAddress, String phoneNumber, boolean experienced, boolean isDriver, String streetAddress, double latitude, double longitude)
  {
    this.name = fullName;
    this.email = emailAddress;
    this.phone = phoneNumber;
    this.exp = experienced;
    this.driver = isDriver;
    this.address = streetAddress;
    this.lat = latitude;
    this.lng = longitude;
  }

  public void print()
  {
    System.out.println("NAME: " + name);
  }

}
