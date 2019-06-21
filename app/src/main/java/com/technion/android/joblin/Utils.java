package com.technion.android.joblin;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.firebase.Timestamp;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Utils {
    private static final String TAG = "Utils";

        public static Point getDisplaySize(WindowManager windowManager){
            try {
                if(Build.VERSION.SDK_INT > 16) {
                    Display display = windowManager.getDefaultDisplay();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    display.getMetrics(displayMetrics);
                    return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
                }else{
                    return new Point(0, 0);
                }
            }catch (Exception e){
                e.printStackTrace();
                return new Point(0, 0);
            }
        }
        public static int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }
    public static void matchPopUp(Context context, String type)
    {
        new SweetAlertDialog(context)
                .setTitleText("It's a match!")
                .setContentText("You can contact this " + type + " now.")
                .setConfirmText("Great!")
                .show();
    }

    public static void newMatchPopUp(Context context, String title, String type)
    {
        new SweetAlertDialog(context)
                .setTitleText(title)
                .setContentText("You can contact this " + type + " now.")
                .setConfirmText("Great!")
                .show();
    }

    public static void resetSwipesPopUp(Context context)
    {
        new SweetAlertDialog(context)
                .setTitleText("It's a brand new day!")
                .setContentText("Your swipes amount has been renewed")
                .setConfirmText("Great!")
                .show();
    }

    public static void noMoreSwipesPopUp(Context context)
    {
        new SweetAlertDialog(context)
                .setTitleText("No more swipes!")
                .setContentText("Try again tommorrow!")
                .setConfirmText("OK")
                .show();
    }

    public static void errorPopUp(Context context, String error)
    {
        new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(String.format("Something went wrong: %s",error))
                .show();
    }

    public static int getAge(
            Date birthDate,
            Date currentDate) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(formatter.format(birthDate));
        int d2 = Integer.parseInt(formatter.format(currentDate));
        int age = (d2 - d1) / 10000;
        return age;
    }

    private static List<String> firstNames = Arrays.asList(
            "Yogev","Amit","Bar","Ben","Kobi","Moti"
            ,"Sagi","Ohad","Matan","Moshe","Liran","Dvir"
            ,"Gal","Zvi","Ofek","Ofir"
    );

    private static List<String> lastNames =  Arrays.asList(
            "Cohen","Levi","Shvartz","Wisemann",
            "Aflalo","Abutbul","Eliyaho","Kahlon",
            "Nitzani","Menhel","Shmoel","Sandler","Mizrahi",
            "Omer"
    );

    private static List<String> skills = Arrays.asList(
            "Java","Python","C++","Management",
            "Soft Skills","C","Web","Graphic Design",
            "Drawing","Android","C#","SQL","JavaScript",
            "NodeJS"
    );

    private static List<String> educationTraits = Arrays.asList(
            "Computer Science","Mathematics","Electronics","Software Engineering",
            "Psychology","Medicine","Graphic Design",
            "Social Studies","Politics","History","English",
            "Physics"
    );

    private static List<String> locationTraits =  Arrays.asList(
            "Tel Aviv","Haifa","Hod HaSharon","Netanya",
            "Ramat Gan","Jerusalem","Holon",
            "Petah Tikva","Nahariya","Kfar Saba","Afula",
            "Hedera", "Tiberia", "Raanana"
    );

    private static List<String> categories = Arrays.asList(
            "Accounting","Computer Science","Education","Finance",
            "IT","Media","Sales"
    );

    public static Candidate getRandomCandidate(){

        Random rand = new Random();
        String firstName = firstNames.get(rand.nextInt(firstNames.size()));
        String lastName = lastNames.get(rand.nextInt(lastNames.size()));
        String email = firstName.toLowerCase() + lastName.toLowerCase() + "@gmail.com";
        String imageUrl = "https://iveybusinessreview.ca/wp-content/uploads/2013/02/Jim-Zhou.png";
        String moreInfo = "This is a dummy account";
        String education = "B.sc in " + educationTraits.get(rand.nextInt(educationTraits.size()));
        Date date = new GregorianCalendar(rand.nextInt(60)+1960, rand.nextInt(12), rand.nextInt(31)).getTime();
        Timestamp birthdate = new Timestamp(date);
        String location = locationTraits.get(rand.nextInt(locationTraits.size()));
        String scope = "Full Time";
        String jobCategory = categories.get(rand.nextInt(categories.size()));
        String firstSkill = skills.get(rand.nextInt(skills.size()));
        String secondSkill = skills.get(rand.nextInt(skills.size()));
        String thirdSkill = skills.get(rand.nextInt(skills.size()));
        List<String> skills = Arrays.asList(firstSkill,secondSkill,thirdSkill);
        List<String> skillsWithoutDup =  new ArrayList<>(new HashSet<>(skills));
        int maxDistance = rand.nextInt(100);

         return new Candidate(email,firstName,
                lastName,imageUrl,birthdate,location,scope,education,skillsWithoutDup,moreInfo,jobCategory,maxDistance);
    }

    public static Recruiter getRandomRecruiter(){
        Random rand = new Random();
        String firstName = firstNames.get(rand.nextInt(firstNames.size()));
        String lastName = lastNames.get(rand.nextInt(lastNames.size()));
        String email = firstName.toLowerCase() + lastName.toLowerCase() + "@gmail.com";
        String imageUrl = "https://iveybusinessreview.ca/wp-content/uploads/2013/02/Jim-Zhou.png";
        String moreInfo = "This is a dummy account";
        String education = "B.sc in " + educationTraits.get(rand.nextInt(educationTraits.size()));
        Date date = new GregorianCalendar(rand.nextInt(60)+1960, rand.nextInt(12), rand.nextInt(31)).getTime();
        Timestamp birthdate = new Timestamp(date);
        String location = locationTraits.get(rand.nextInt(locationTraits.size()));
        String scope = "Full Time";
        String jobCategory = categories.get(rand.nextInt(categories.size()));
        String firstSkill = skills.get(rand.nextInt(skills.size()));
        String secondSkill = skills.get(rand.nextInt(skills.size()));
        String thirdSkill = skills.get(rand.nextInt(skills.size()));
        String workplace = "Random";
        List<String> skills = Arrays.asList(firstSkill,secondSkill,thirdSkill);
        List<String> skillsWithoutDup =  new ArrayList<>(new HashSet<>(skills));

        return new Recruiter(email,firstName,
                lastName,imageUrl,workplace,jobCategory,scope,location,moreInfo,education,skillsWithoutDup);
    }

}
