-- Queries
-- view all countries
SELECT * FROM Country;

-- Show booking details with user and accommodation
SELECT b.BookingID, u.Name, a.AccommodationName
FROM Booking b
JOIN Users u ON b.UserID = u.UserID
JOIN Accommodation a ON b.AccommodationID = a.AccommodationID;

-- Show cities in a specific country
SELECT City.CityName, Country.CountryName
FROM City
JOIN Country ON City.CountryID = Country.CountryID
WHERE Country.CountryName = 'India';

-- Show reviews with user name and tourist place
SELECT u.Name, t.PlaceName, r.Rating, r.Comment
FROM Review r
JOIN Users u ON r.UserID = u.UserID
JOIN TouristPlace t ON r.PlaceID = t.PlaceID;

-- Show affordable accommodations (≤ 3000)
SELECT DISTINCT Country.CountryName, City.CityName, Accommodation.PricePerNight
FROM Accommodation
JOIN City ON Accommodation.CityID = City.CityID
JOIN Country ON City.CountryID = Country.CountryID
WHERE Accommodation.PricePerNight <= 3000;

-- Show accommodations with city name
SELECT a.AccommodationName, a.Type, a.PricePerNight, c.CityName
FROM Accommodation a
JOIN City c ON a.CityID = c.CityID;

