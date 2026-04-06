-- procedures

-- show user bookings 
delimiter //
create procedure showUserBookings(
in in_username varchar(50)
)
begin

select u.Name, b.BookingID, b.BookingDate, a.AccommodationName
from Booking b
join Users u on b.UserID = u.UserID
join Accommodation a on b.AccommodationID = a.AccommodationID
where u.Name = in_username;

end;
//
delimiter ;
call showUserBookings('Indrani');

-- add booking
DELIMITER //
CREATE PROCEDURE addBooking(
IN p_bookingid INT,
IN p_bookingdate DATE,
IN p_userid INT,
IN p_accommodationid INT
)
BEGIN

INSERT INTO Booking (BookingID, BookingDate, UserID, AccommodationID)
VALUES (p_bookingid, p_bookingdate, p_userid, p_accommodationid);

END //
DELIMITER ;

CALL addBooking(5,'2026-03-20',1,2);

-- showing particula city accommodations
delimiter //
create procedure showCityAccommodations(
in p_cityname varchar(50)
)
begin

select a.AccommodationName, a.Type, a.PricePerNight
from Accommodation a
join City c on a.CityID = c.CityID
where c.CityName = p_cityname;

end //
delimiter ;
call showCityAccommodations('Mumbai');
