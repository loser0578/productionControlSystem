在此统一管理所有 sql，优点有：
1：避免在 JFinalClubConfig 中一个个添加 sql 模板文件
2：免除在实际的模板文件中书写 namespace，以免让 sql 定义往后缩进一层
3：在此文件中还可以通过 define 指令定义一些通用模板函数，供全局共享
   例如定义通用的 CRUD 模板函数

#namespace("index")
#include("index.sql")
#end

#namespace("project")
#include("project.sql")
#end

#namespace("dept")
#include("sys/dept.sql")
#end

#namespace("role")
#include("sys/role.sql")
#end

#namespace("menu")
#include("sys/menu.sql")
#end

#namespace("specification")
#include("shop/specification.sql")
#end

#namespace("blessing")
#include("shop/blessing.sql")
#end

#namespace("denomination")
#include("shop/denomination.sql")
#end

#namespace("goodsspecification")
#include("shop/goodsspecification.sql")
#end

#namespace("feedback")
#include("feedback.sql")
#end

#namespace("admin.auth")
#include("admin_auth.sql")
#end

#namespace("category")
#include("shop/category.sql")
#end

#namespace("categoryclassfy")
#include("shop/categoryclassfy.sql")
#end

#namespace("shipping")
#include("shop/shipping.sql")
#end

#namespace("goods")
#include("shop/goods.sql")
#end

#namespace("order")
#include("shop/order.sql")
#end


#namespace("shopuser")
#include("shop/user.sql")
#end

#namespace("ordergoods")
#include("shop/ordergoods.sql")
#end

#namespace("coupon")
#include("shop/coupon.sql")
#end

#namespace("usercoupon")
#include("shop/usercoupon.sql")
#end

#namespace("sysuser")
#include("sys/user.sql")
#end

#namespace("product")
#include("shop/product.sql")
#end

#namespace("goodsgallery")
#include("shop/goodsgallery.sql")
#end