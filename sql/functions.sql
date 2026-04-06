-- functions

-- total bookings of an user
delimiter //
create function totalBookings(uid int)
returns int
reads sql data
begin

declare total int;

select count(*) into total
from Booking
where UserID = uid;

return total;

end;
//
delimiter ;
select totalBookings(1);

-- average accommodation price
delimiter //
create function avgAccommodationPrice(p_city varchar(50))
returns decimal(10,2)
reads sql data
begin
declare avgprice decimal(10,2);

select avg(a.pricepernight) into avgprice
from accommodation a
join city c on a.cityid = c.cityid
where c.cityname = p_city;

return avgprice;

end //
delimiter ;
select avgAccommodationPrice('Mumbai');
