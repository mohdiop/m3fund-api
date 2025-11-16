package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.AdminDashboardResponse;
import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.entity.Payment;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.User;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import com.mohdiop.m3fundapi.repository.PaymentRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class StatsService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PaymentRepository paymentRepository;

    public StatsService(UserRepository userRepository, ProjectRepository projectRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.paymentRepository = paymentRepository;
    }

    public AdminDashboardResponse getDashboardStats() {
        var allUsers = userRepository.findAll();
        Long totalActiveUsers = allUsers.stream()
                .filter(user -> user.getState() == UserState.ACTIVE)
                .count();
        double lastMonthAverage = getDailyAverageCreation(
                allUsers,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().minus(1)),
                u -> ((User) u).getUserCreatedAt()
        );
        double lastMonthStdDev = getMonthlyStandardDeviation(
                allUsers,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().minus(1)),
                u -> ((User) u).getUserCreatedAt()
        );
        double currentMonthAverage = getDailyAverageCreation(
                allUsers,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth()),
                u -> ((User) u).getUserCreatedAt()
        );
        double currentMonthStdDev = getMonthlyStandardDeviation(
                allUsers,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth()),
                u -> ((User) u).getUserCreatedAt()
        );
        double usersLastMonthScore = lastMonthAverage / (lastMonthStdDev + 1);
        double usersCurrentMonthScore = currentMonthAverage / (currentMonthStdDev + 1);
        var allProjects = projectRepository.findAll();
        Long totalActiveProjects = allProjects.stream()
                .filter(Project::isValidated)
                .count();
        lastMonthAverage = getDailyAverageCreation(
                allProjects,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().minus(1)),
                p -> ((Project) p).getCreatedAt()
        );
        currentMonthAverage = getDailyAverageCreation(
                allProjects,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth()),
                p -> ((Project) p).getCreatedAt()
        );
        lastMonthStdDev = getMonthlyStandardDeviation(
                allProjects,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().minus(1)),
                p -> ((Project) p).getCreatedAt()
        );
        currentMonthStdDev = getMonthlyStandardDeviation(
                allProjects,
                YearMonth.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth()),
                p -> ((Project) p).getCreatedAt()
        );
        double projectsLastMonthScore = lastMonthAverage / (lastMonthStdDev + 1);
        double projectsCurrentMonthScore = currentMonthAverage / (currentMonthStdDev + 1);
        var allPayments = paymentRepository.findAll().stream().map(Payment::toResponse).toList();
        double totalFund = 0D;
        for (PaymentResponse payment : allPayments) {
            totalFund+=payment.amount();
        }

        return new AdminDashboardResponse(
                totalActiveUsers,
                countUsersCreatedInCurrentMonth(allUsers),
                usersCurrentMonthScore,
                usersLastMonthScore,
                totalActiveProjects,
                countProjectsCreatedInCurrentMonth(allProjects),
                projectsCurrentMonthScore,
                projectsLastMonthScore,
                totalFund,
                totalFund,
                allPayments,
                allProjects.stream().map(Project::toResponse).toList(),
                allUsers.stream().map(User::toSimpleResponse).toList()
        );
    }

    public long countUsersCreatedInCurrentMonth(List<User> users) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        return users.stream()
                .map(User::getUserCreatedAt)
                .map(LocalDateTime::toLocalDate)
                .filter(date -> !date.isBefore(start) && !date.isAfter(end))
                .count();
    }

    public long countProjectsCreatedInCurrentMonth(List<Project> projects) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        return projects.stream()
                .map(Project::getCreatedAt)
                .map(LocalDateTime::toLocalDate)
                .filter(date -> !date.isBefore(start) && !date.isAfter(end))
                .count();
    }

    public double getDailyAverageCreation(
            List<?> entities,
            YearMonth month,
            Function<Object, LocalDateTime> createdAtExtractor
    ) {

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        int daysInMonth = month.lengthOfMonth();

        long totalInMonth = entities.stream()
                .map(createdAtExtractor)
                .map(LocalDateTime::toLocalDate)
                .filter(date -> !date.isBefore(start) && !date.isAfter(end))
                .count();

        return (double) totalInMonth / daysInMonth;
    }


    public double getMonthlyStandardDeviation(
            List<?> entities,
            YearMonth month,
            Function<Object, LocalDateTime> createdAtExtractor
    ) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Double> dailyCounts = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            dailyCounts.add(0.0);
        }
        for (Object entity : entities) {
            LocalDate date = createdAtExtractor.apply(entity).toLocalDate();
            if (!date.isBefore(start) && !date.isAfter(end)) {
                int index = date.getDayOfMonth() - 1;
                dailyCounts.set(index, dailyCounts.get(index) + 1);
            }
        }
        int n = dailyCounts.size();
        double mean = dailyCounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = dailyCounts.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum() / n;
        return Math.sqrt(variance);
    }


}
